using Redis.OM.Modeling;

namespace dotnetcsharp;

public class Office
{
    [Searchable] public string? Address { get; set; }
    [Indexed] public string? AddressNumber { get; set; }
    [Indexed] public string? City { get; set; }
    [Indexed] public string? State { get; set; }
    [Indexed] public string? PostalCode { get; set; }
    [Indexed] public string? Country { get; set; }
}