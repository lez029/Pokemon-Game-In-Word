param(
    [string]$JsonJar = "$env:USERPROFILE/.m2/repository/org/json/json/20240303/json-20240303.jar"
)

$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    if (!(Test-Path -LiteralPath $JsonJar)) {
        throw 'Provide the org.json dependency using -JsonJar <path>.'
    }
    New-Item -ItemType Directory -Force out/verification | Out-Null
    # JSON export needs Jackson; it is outside these offline battle checks.
    $sources = Get-ChildItem src -Recurse -Filter '*.java' |
        Where-Object { $_.Name -notin @('JSONDataWriter.java', 'MoveParser.java') } |
        ForEach-Object { $_.FullName }
    $tests = Get-ChildItem tests -Filter '*Test.java' | ForEach-Object { $_.FullName }
    & javac -encoding UTF-8 -cp $JsonJar -d out/verification $sources $tests
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    foreach ($test in (Get-ChildItem tests -Filter '*Test.java')) {
        & java -ea -cp out/verification $test.BaseName
        if ($LASTEXITCODE -ne 0) { throw "Regression checks failed: $($test.BaseName)" }
    }
} finally {
    Pop-Location
}
