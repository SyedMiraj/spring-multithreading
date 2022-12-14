# Multithreading using @Async in Spring Boot

If you want to be a better Java developer and want to write code that executes within a short span of time by utilizing maximum of CPUs then multithreading will be the key. Multithreading is basically a process of concurrent execution for two or more parts of a task and each part of that task is called Thread. Spring provides an easy way of handling complex tasks, by abstractions for asynchronous execution of tasks by using the TaskExecutor interface. It is  very much useful as it runs complex activities in the background without interfering with the main program.


## Project Overview:

We will create a simple java web project from spring.io. This application will have the following functionalities:
- Accepts users data from csv file and store in H2 database.
- Storing the data in the database in a separate thread instead of the main thread.
- Retrieve data from the database which also uses a separate thread instead of the main thread.

![Spring Initializer](https://github.com/SyedMiraj/spring-multithreading/blob/main/src/main/resources/static/spring-initializer.png)

## Async Configuration:

We will configure the asynchronous execution in AsyncConfig class. Before going to code work, we need to understand the TaskExecutor interface. Some commonly used implementations are ThreadPoolTaskExecutor, ThreadPoolTaskScheduler and SimpleAsyncTaskExecutor. 

- **ThreadPoolTaskExecutor:** Tasks are distributed among the threads in the thread pool. When a thread completes executing a task, it returns to the pool.
- **ThreadPoolTaskScheduler:** This executor is useful for scheduler tasks.
- **SimpleAsyncTaskExecutor:** it is the default executor for asynchronous tasks. By default, the number of concurrent threads is unlimited. 

In our example we will use ThreadPoolTaskExecutor to configure asynchronous method execution. 

````
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("UserThread-");
        executor.initialize();
        return executor;
    }
    
}
````
Here **@Configuration** will indicate that AyncConfig class has @Bean definition methods and **@EnableAsync** will enable asynchronous process. Here core pool size set to 2 means 2 threads will be always present in the core pool. Max pool size set to 2 to make sure the most threads that can be produced will be 2. Thread name prefix is set to 'UserThread-' and will be helpful to identify the threads that are executing the asynchronous operation. Here initialization will create the blocking queue and the ThreadPoolExecutor. One important thing is to specify the bean name taskExecutor as Spring will specifically look for this bean when the server is started. If this bean is not defined, Spring will create a SimpleAsyncTaskExecutor by default.

## API development:

We will create two apis. One for saveUsers data and one for retrieveUsers data. When saveUsers api will be called a multipart file will send as request parameter and response will void. Data will be parsed and saved using a separate thread and we will monitor the thread name by logging. Similarly, for the retreiveUser api, no parameter will be passed and return type will be a list of users wrapped around CompletableFuture object. The CompletableFuture, introduced in Java 8, provides an easy way to write asynchronous, non-blocking and multi-threaded code. 

In this project, we will use sample data about Users. Mock data is generated from [Mockaroo](https://www.mockaroo.com/). We will create a JPA entity User.
````
@Entity
@Table(name = "USERS_DATA")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String gender;
    private String country;

}
````

and a corresponding JPA repository. 

````
public interface UserRepository extends JpaRepository<User, Long> {
}
````

In the controller layer, saveUsers api is mapped with @PostMapping and retrieveUsers is mapped with @GetMapping.

````
@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity saveUsers(@RequestParam(name = "file") MultipartFile files){
        service.saveUsers(files);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<User>>> retrieveUsers(){
        CompletableFuture<List<User>> users = service.retrieveUsers();
        return users.thenApply(ResponseEntity::ok);
    }
}
````

In the service layer, @Async annotation in the method level will create a seperate thread for executing the operation. 

````
@Service
@Slf4j
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Async
    public void saveUsers(MultipartFile files) {
        List<User> users = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(files.getInputStream()))) {
                String line;
                while ((line=br.readLine()) != null) {
                    final String[] data=line.split(",");
                    final User user = new User();
                    user.setName(data[0]);
                    user.setCountry(data[1]);
                    user.setGender(data[2]);
                    users.add(user);
                }
            } catch(final IOException e) {
            log.error("Failed to parse CSV file {}", e);
        }
        if(!users.isEmpty()){
            log.info("Current Thread: " + Thread.currentThread().getName());
            repository.saveAll(users);
        }
    }

    @Async
    public CompletableFuture<List<User>> retrieveUsers() {
        log.info("Current Thread: " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(repository.findAll());
    }
}
````

## API Testing

We will first test the saveUsers api for storing data from csv file. For testing purpose we will use postman. 
![POST call](https://github.com/SyedMiraj/spring-multithreading/blob/main/src/main/resources/static/post-call.png)
Data is saved successfully with Http 200 response. @Async method will use seperate thread which is logged in console.
![POST call](https://github.com/SyedMiraj/spring-multithreading/blob/main/src/main/resources/static/post-thread-console.png)

Now we will test the retrieveUsers api for retrieveing the data from database. 
![POST call](https://github.com/SyedMiraj/spring-multithreading/blob/main/src/main/resources/static/get-call.png)
In the console it shows that seperate thread is used for retreiving the data.
![POST call](https://github.com/SyedMiraj/spring-multithreading/blob/main/src/main/resources/static/get-thread-console.png)

## Conclusion:

In this article we have learned multithreading for spring boot with four key points. **@EnableAsync** will look for methods marked with @Async annotation and run them in background thread pools. The **Executor interface** provides methods to manage termination and methods that can produce a Future. The **@Async** annotated methods are executed in a separate thread and return **CompletableFuture** to hold the result of an asynchronous computation.
