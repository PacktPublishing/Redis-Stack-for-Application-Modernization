import { createClient } from 'redis';

const client = createClient({
    password: process.env.redis_password,
    username: process.env.redis_password || 'default',
    socket: {
        host: process.env.redis_hostname || '127.0.0.1',
        port: process.env.redis_port || 6379
    }
});

(async () => {
    console.log('Connecting...');
    await client.connect();
})();

client.on('error', (err) => {
    console.log('Could not establish a connection with redis. ' + err);
});

client.on('connect', function (err) {
    console.log('Connected to Redis Stack successfully');
});



let resp = await client.set('client', 'redis', function(err, reply) {
	console.log(err);
	console.log(reply);
});
console.log('Set: ' + resp);

resp = await client.get('client', function(err, reply) {
	console.log(err);
	console.log(reply);
});
console.log('Val: ' + resp);



let result = await client.lPush("queue:tasks", "Task-1");
console.log("numberOfTasks {}"+ result);
result = await client.lPush("queue:tasks", "Task-2");
console.log("numberOfTasks {}"+ result);
result = await client.rPop("queue:tasks");
console.log("Retrieved task {}"+ result);


result = await client.sAdd('employee:ids', '00000001');
console.log('Added employee:00000001 - ' + result);
result = await client.sAdd('employee:ids', '00000002');
console.log('Added employee:00000002 - ' + result);
result = await client.sAdd('employee:ids', '00000001');
console.log('Added employee:00000001 - ' + result);
let employeeIds = await client.sMembers('employee:ids');
console.log('Employee IDs: ' + employeeIds);
employeeIds.forEach(employee => {
    console.log('Employee ID: ' + employee);
});
let exists = await client.sIsMember('employee:ids', '00000001');
console.log('Employee ID-00000001 exists? ' + exists);


const players = [
    {
        score: 22.0,
        value: "PlayerOne"
    },
    {
        score: 10.0,
        value: "PlayerTwo"
    },
    {
        score: 78.0,
        value: "PlayerThree"
    }
];
console.log('Players map:' + players);
let added = await client.zAdd('players', players);
console.log('Added players: ' + added);
let playersWithScores = await client.zRangeByScore("players", 0, 50);
console.log('Players with score in range 0-50: ' + playersWithScores);
playersWithScores.forEach(element => {
    console.log('Player with score: ' + element);
});
let score = await client.zScore("players", "PlayerTwo");
console.log('Player PlayerTwo with score: ' + score);


let user = {'firstname':'Luigi','lastname':'Fugaro','username':'foogaro'}
console.log('User: ' + user);
let fieldValuePairs = await client.hSet('user:1', user);
console.log('User fields added in Hash: ' + fieldValuePairs);
let email = await client.hSet('user:1', 'email', 'luigi@foogaro.com');
console.log('Email added: ' + email);
let firstname = await client.hGet('user:1', 'firstname');
console.log('User firstname: ', firstname);
let fields = await client.hGetAll('user:1');
console.log('All fields: ', JSON.parse(JSON.stringify(fields)));


await client.quit();