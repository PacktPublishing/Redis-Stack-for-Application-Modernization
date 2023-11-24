using Redis.OM.Modeling;

namespace dotnetcsharp;

[Document(StorageType = StorageType.Json, Prefixes = new []{"Employee"})]
public class Employee
{
    [RedisIdField] [Indexed] public string? Id { get; set; }
    [Indexed] public string? FirstName { get; set; }
    [Indexed] public string? LastName { get; set; }
    [Indexed] public int? Age { get; set; }
    [Searchable] public string? FunFact { get; set; }
    [Indexed(CascadeDepth = 1)] public Office? Office { get; set; }
    [Indexed] public string[] Roles { get; set; } = Array.Empty<string>();
}