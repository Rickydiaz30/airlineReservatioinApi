param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[a-z0-9][a-z0-9_-]*$')]
    [string]$DockerHubUser,

    [string]$Repository = 'group3-airways-api',

    [switch]$BuildOnly
)

$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path

Push-Location $projectRoot
try {
    $changes = @(git status --porcelain)
    if ($LASTEXITCODE -ne 0) { throw 'Could not read Git status.' }
    if ($changes.Count -gt 0) {
        throw 'Commit or remove local changes before publishing. The image tag must match its Git commit.'
    }

    $commit = (git rev-parse --short=12 HEAD).Trim()
    if ($LASTEXITCODE -ne 0) { throw 'Could not read Git commit.' }

    $image = "${DockerHubUser}/${Repository}:sha-${commit}"
    Write-Host "Building $image"
    docker build --tag $image .
    if ($LASTEXITCODE -ne 0) { throw 'Docker build failed.' }

    if (-not $BuildOnly) {
        Write-Host "Pushing $image"
        docker push $image
        if ($LASTEXITCODE -ne 0) { throw 'Docker push failed. Run docker login and try again.' }
    }

    Write-Host "API_IMAGE=$image"
}
finally {
    Pop-Location
}
