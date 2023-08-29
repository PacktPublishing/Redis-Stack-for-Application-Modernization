from typing import List

from redis_om import EmbeddedJsonModel, Field, JsonModel, get_redis_connection, Migrator

redisClient = get_redis_connection(
    host='<YOUR_HOSTNAME>',
    port=<YOUR_PORT>,
    username='<YOUR_USERNAME>',
    password='<YOUR_PASSWORD>',
    decode_responses=True
)


class Office(EmbeddedJsonModel):
    address: str = Field(index=True)
    address_number: int = Field(index=True)
    city: str = Field(index=True)
    state: str = Field(index=True)
    postal_code: str = Field(index=True)
    country: str = Field(index=True, default="Remote")

    class Meta:
        database = redisClient


class Employee(JsonModel):
    firstname: str = Field(index=True)
    lastname: str = Field(index=True)
    age: int = Field(index=True)
    office: Office
    roles: List[str] = Field(index=True)
    fun_fact: str = Field(index=True, full_text_search=True)

    class Meta:
        database = redisClient


Migrator().run()

new_office = Office(
    address="Via Italia",
    address_number=1,
    city="Roma",
    state="Roma",
    postal_code="00100",
    country="Italy"
)
new_employee = Employee(
    firstname="Luigi",
    lastname="Fugaro",
    age=44,
    office=new_office,
    roles=["Solution Architect"],
    fun_fact="No matter what, we are all in sales!"
)
new_employee.save()

# Update
new_employee.age = 45
new_employee.save()

# Find by ID
employee = Employee.get(1)

# Find by matching first name and last name:
employees = Employee.find(
      (Employee.first_name == "Luigi") &
      (Employee.last_name == "Fugaro")
  ).all()

# Find by age range:
employees = Employee.find(
      (Employee.age >= 35) &
      (Employee.age <= 45)
  ).sort_by("age").all()

# Find by a role and office:
employees = Employee.find(
      (Employee.roles << "Solution Architect") &
      (Employee.office.city == "Rome")
  ).all()

# Find by "fun fact" using full text search:
employees = Employee.find(Employee.fun_fact % "we are all").all()

# Delete by ID
Employee.delete(1)