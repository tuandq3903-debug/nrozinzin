$f=[System.IO.File]::ReadAllText('D:\nro zin zin\NRO ZINZIN2025 V3\NRO ZINZIN\database\nro.sql')
$lines = $f -split "`n"
$startIdx = 8526
$block = ($lines[$startIdx..($lines.Count-1)] -join "`n")
# Extract skill IDs - in the JSON inside skills column there are "id":N where N is skill point id
$ids = [regex]::Matches($block, '"id":(\d+)') | ForEach-Object { [int]$_.Groups[1].Value } | Sort-Object -Unique
Write-Host "Total unique skill IDs: $($ids.Count)"
$max = ($ids | Measure-Object -Maximum).Maximum
$min = ($ids | Measure-Object -Minimum).Minimum
Write-Host "Min: $min, Max: $max"
Write-Host "Last 20 IDs:"
($ids | Select-Object -Last 20) -join ","

Write-Host "`nSkill template IDs (col 2):"
$colids = [regex]::Matches($block, '^\((\d+),(\d+),') | ForEach-Object { [int]$_.Groups[2].Value } | Sort-Object -Unique
Write-Host "Total: $($colids.Count)"
Write-Host "Min: $(($colids | Measure-Object -Minimum).Maximum), Max: $(($colids | Measure-Object -Maximum).Maximum)"
($colids | Select-Object -Last 20) -join ","
