$client = New-Object System.Net.Sockets.TcpClient("localhost", 2000)
$stream = $client.GetStream()
$writer = New-Object System.IO.StreamWriter($stream)
$reader = New-Object System.IO.StreamReader($stream)
$writer.AutoFlush = $true

$writer.WriteLine("EUR")

while ($line = $reader.ReadLine()) { Write-Host $line }
$client.Close()