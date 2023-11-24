using Redis.OM;

namespace dotnetcsharp
{
    class RedisOM
    {
        public RedisOM()
        {
            Office romeOffice = new Office();
            romeOffice.Address = "Via Roma";
            romeOffice.AddressNumber = "1";
            romeOffice.PostalCode = "00100";
            romeOffice.City = "Rome";
            romeOffice.Country = "Italy";
            romeOffice.State = "Italy";
            Console.WriteLine("Office: {0}", romeOffice);

            Office macerataOffice = new Office();
            romeOffice.Address = "Via Macerata";
            romeOffice.AddressNumber = "1";
            romeOffice.PostalCode = "62100";
            romeOffice.City = "Macerata";
            romeOffice.Country = "Italy";
            romeOffice.State = "Italy";
            Console.WriteLine("Office: {0}", macerataOffice);

            Employee luigi = new Employee();
            luigi.FirstName = "Luigi";
            luigi.LastName = "Fugaro";
            luigi.Age = 44;
            luigi.FunFact = "What goes around, comes around!";
            luigi.Roles = new[] { "Solution Architect" };
            luigi.Office = romeOffice;
            Console.WriteLine("Employee: {0}", luigi);

            Employee mirko = new Employee();
            mirko.FirstName = "Mirko";
            mirko.LastName = "Ortensi";
            mirko.Age = 44;
            mirko.FunFact = "Sing a song and jump around!";
            mirko.Roles = new[] { "Technical Enablement Architect" };
            mirko.Office = macerataOffice;
            Console.WriteLine("Employee: {0}", mirko);

            var provider = new RedisConnectionProvider("redis://<USERNAME:PASSWORD>@localhost:6379");
            var employees = provider.RedisCollection<Employee>();
            var connection = provider.Connection;
            var indexCreated = connection.CreateIndex(typeof(Employee));
            Console.WriteLine("Index created? {0}", indexCreated);

            var employeeLuigi = employees.InsertAsync(luigi);
            Console.WriteLine("Employee ID: {0}", employeeLuigi.Result);
            var employeeMirko = employees.InsertAsync(mirko);
            Console.WriteLine("Employee ID: {0}", employeeMirko.Result);

            var loo = connection.GetAsync<Employee>(employeeLuigi.Result);
            Console.WriteLine("GetAsync Employee LastName: {0}", loo.Result?.LastName);
            var mee = employees.FindByIdAsync(employeeMirko.Result);
            Console.WriteLine("FindByIdAsync Employee LastName: {0}", mee.Result?.LastName);
            
            var empsByExactMatch = employees.Where(x => x.FirstName == "Luigi");
            foreach (var emp in empsByExactMatch)
            {
                Console.WriteLine($"{emp.FirstName} is {emp.Age} years old and works as {emp.Roles[0]}!");
            }

            var empsByFullText = employees.Where(e => e.FunFact == "around");
            foreach (var emp in empsByFullText)
            {
                Console.WriteLine($"{emp.FirstName} is {emp.Age} years old and works as {emp.Roles[0]}!");
            }
            
        }
    }
}




