import { createClient } from 'redis';
import {EntityId, EntityKeyName, Schema} from 'redis-om'
import { Repository } from 'redis-om'

const redisClient = createClient({
    password: process.env.redis_password,
    username: process.env.redis_password || 'default',
    socket: {
        host: process.env.redis_hostname || '127.0.0.1',
        port: process.env.redis_port || 6379
    }
});


(async () => {
    console.log('Connecting...');
    await redisClient.connect();
})();

redisClient.on('error', (err) => {
    console.log('Could not establish a connection with Redis Stack. ' + err);
});

redisClient.on('connect', function (err) {
    console.log('Connected to Redis Stack successfully');
});


const employeeSchema = new Schema('Employee', {
    firstName: { type: 'string' },
    lastName: { type: 'string' },
    age: { type: 'number' },
    roles: { type: 'string[]' },
    office: { type: 'text' },
    funFact: { type: 'text' }
},{
    dataStructure: 'JSON'
})

export const employeeRepository = new Repository(employeeSchema, redisClient);

await employeeRepository.createIndex();

let luigi = {
    'firstName': 'Luigi',
    'lastName': 'Fugaro',
    'age': 45,
    'roles': ['Solution Architect'],
    'office': 'Roma',
    'funFact': 'I still play PAC-MAN.'
}
console.log('Luigi: ' + luigi);
let mirko = {
    'firstName': 'Mirko',
    'lastName': 'Ortensi',
    'age': 46,
    'roles': ['Techincal Architect'],
    'office': 'Macerata',
    'funFact': 'I still play Arkanoid.'
}
console.log('Mirko: ' + mirko);

const luigiEmployee = await employeeRepository.save(luigi)
const mirkoEmployee = await employeeRepository.save(mirko)
console.log('luigiEmployee.EntityId: ' + luigiEmployee[EntityId]);
console.log('luigiEmployee.EntityKeyName: ' + luigiEmployee[EntityKeyName]);
console.log('mirkoEmployee.EntityId: ' + mirkoEmployee[EntityId]);
console.log('mirkoEmployee.EntityKeyName: ' + mirkoEmployee[EntityKeyName]);

let employees = await employeeRepository.search().return.all();
console.log('employees: ' + employees);
printThemAll('Employee: ',employees);
/*
employees.forEach(employee => {
    console.log('Employee: ' + employee);
});
*/

employees = await employeeRepository.search().where('firstName').eq('Mirko').return.all()
printThemAll('Search for firstname=Mirko',employees);
employees = await employeeRepository.search().where('firstName').does.not.eq('Mirko').return.all()
printThemAll('Search for firstname!=Mirko',employees);
employees = await employeeRepository.search().where('age').is.between(40, 50).return.all()
printThemAll('Search for age=40..50',employees);
employees = await employeeRepository.search().where('roles').contains('Technical Architect').return.all()
printThemAll('Search for roles=\'Technical Architect\'',employees);
employees = await employeeRepository.search().where('funFact').match('*play*').return.all()
printThemAll('Search for funFact=*play*',employees);

await redisClient.quit();

function printThemAll(msg,elems) {
    elems.forEach(elem => {
        console.log(msg,elem);
    });
}
