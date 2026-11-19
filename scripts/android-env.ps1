$env:JAVA_HOME = (Get-ChildItem "$PSScriptRoot/../.tools/jdk" -Directory | Select-Object -First 1).FullName
$env:ANDROID_HOME = [IO.Path]::GetFullPath("$PSScriptRoot/../.tools/android-sdk")
$env:GRADLE_USER_HOME = [IO.Path]::GetFullPath("$PSScriptRoot/../.tools/gradle-home")
$env:Path = "$env:JAVA_HOME/bin;$env:ANDROID_HOME/platform-tools;$env:Path"
