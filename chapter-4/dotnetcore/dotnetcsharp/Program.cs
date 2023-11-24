using dotnetcsharp;
using StackExchange.Redis;

ConnectionMultiplexer redis = ConnectionMultiplexer.Connect("redis-13461.c238.us-central1-2.gce.cloud.redislabs.com:13461,password=redis-stack");
IDatabase db = redis.GetDatabase();

db.Execute("FLUSHALL");

db.StringSet("client", "NRedisStack");
Console.WriteLine("Retrieved value {0} for key client", db.StringGet("client"));

long numberOfTasks = db.ListLeftPush("queue:tasks", "Task-1");
Console.WriteLine("Number of tasks: {0}", numberOfTasks);
numberOfTasks = db.ListLeftPush("queue:tasks", "Task-2");
Console.WriteLine("Number of tasks: {0}", numberOfTasks);
string task = db.ListRightPop("queue:tasks").ToString();
Console.WriteLine("Retrieved task: {0}", task);

db.SetAdd("employee:ids", "00000001");
db.SetAdd("employee:ids", "00000002");
db.SetAdd("employee:ids", "00000001");
RedisValue[] employeeIds = db.SetMembers("employee:ids");
Console.WriteLine("Employee IDs {0}", employeeIds);
Array.ForEach(employeeIds, employeeId => Console.WriteLine("Employee: {0}", employeeId)); 
bool exists = db.SetContains("employee:ids", "00000001"); 
Console.WriteLine("Employee id \"00000001\" exists {0}", exists);

SortedSetEntry[] scores = new SortedSetEntry[3];
scores[0] = new SortedSetEntry("PlayerOne", 22.0); 
scores[1] = new SortedSetEntry("PlayerTwo", 10.0); 
scores[2] = new SortedSetEntry("PlayerThree", 78.0); 
Console.WriteLine("Scores map {0}", scores); 
long addedScores = db.SortedSetAdd("players", scores); 
Console.WriteLine("Added scores {0}", addedScores); 
RedisValue[] players = db.SortedSetRangeByScore("players", 0, 50); 
Console.WriteLine("Players with score in range 0-50:");
Array.ForEach(players, player => Console.WriteLine(player)); 
double score = db.SortedSetScore("players", "PlayerTwo").Value; 
Console.WriteLine("Score for \"PlayerTwo\" {0}", score);

HashEntry[] user = new HashEntry[3];
user[0] = new HashEntry("FirstName", "Luigi");
user[1] = new HashEntry("LastName", "Fugaro");
user[2] = new HashEntry("UserName", "foogaro");
db.HashSet("user:1", user);
string firstName = db.HashGet("user:1", "FirstName").ToString();
Console.WriteLine("User firstname {0}", firstName);
HashEntry[] userFields = db.HashGetAll("user:1");
Array.ForEach(userFields, userField => Console.WriteLine("Field {0}: {1}", userField.Name, userField.Value));

RedisOM rom = new RedisOM();
