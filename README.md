# 🎋 Intro:
This project is an HTTP/REST-based server built as a platform for trading and battling with and against each other in a magical card-game world.   

# 📦 Technologies:
• Java      
• REST API    
• PostgreSQL       
• JUnit      
• Multithreading

# 👩🏽‍🍳 Features:
• a user can be registered as a player with credentials (unique username, password)   
• a user can manage his cards   
• a card consists of: a name and multiple attributes (damage, element type)   
• a card is either a spell card or a monster card   
• a user has multiple cards in his stack   
• a stack is the collection of all his current cards (hint: cards can be removed by trading)   
• a user can buy cards by acquiring packages   
• a package consists of 5 cards and can be acquired from the server by paying 5 virtual coins        
• every user has 20 coins to buy (4) packages    
• the best 4 cards are selected by the user to be used in the deck    
• the deck is used in the battles against other players     
• a battle is a request to the server to compete against another user with your currently defined deck   

# 💭 Process:
The project was initialized according to the best practices of building server backend applications with RESTful APIs. First, classes and game mechanics were defined and created according to the features described above. Afterwards, a RESTful API was built with various endpoints and respective request requirements to enable numerous features, such as user registration, token-based log in, card purchase, battle initialisation etc. The next step included creating a PostgreSQL database and defining respective schemas: User, Card and TradingDeal (specifications in file "db_conf.txt"). Upon establishing the database connection several layers according to the Controller-Service-Repository were defined and implemented: Controllers are responsible for directly handling HTTP requests and sending responses back to clients, Services handle application logic and make calls to repositories if necessary (services are also the only ones with access to the repositories, while repositories are implemented as singletons that implement respective interfaces - otherwise it would violate the Dependency Inversion Principle), and Repositories directly access the database and perform the operations. Eventually, multithreading was integrated to handle multiple clients at once.    

The project was implemented with core principles of TDD (test-driven development) in mind: a unit test for each core feature was written before implementing it.

# 📚 Learnings:
This project gave a solid insight into building robust, reliable backend servers for web applications. One of the most challenging aspects of it was piecing all things together and developing a whole system whose parts would work cohesively. While handling database queries and HTTP requests is relatively simple, defining the business logic for this advanced multifaceted card game and carefully monitoring the battle outcomes was much more sophisticated.

# ✨ Improvements:
The game simply works as a backend server that needs to accept raw HTTP queries, because the frontend has not been implemented yet.

# 🚦 Running the Project:
1. Clone the repository
2. Create a docker container from Dockerfile
3. For experiencing full functionality, one can simply execute the .bat file and watch the HTTP requests being sent while receiving respective responses from the server
