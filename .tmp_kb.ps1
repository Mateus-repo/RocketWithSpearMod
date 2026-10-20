$javap = 'C:\Program Files\Java\jdk-25.0.4\bin\javap.exe'
$jar = 'C:\Users\Strefiz\.gradle\caches\fabric-loom\minecraftMaven\net\minecraft\minecraft-merged-deobf\26.3\minecraft-merged-deobf-26.3.jar'
$out = 'C:\Users\Strefiz\Documents\GitHub\RocketWithSpearMod\.tmp_mpgm.txt'
& $javap -classpath $jar -p -c net.minecraft.client.multiplayer.MultiPlayerGameMode 2>&1 | Set-Content -Path $out
Write-Host "written $out"
