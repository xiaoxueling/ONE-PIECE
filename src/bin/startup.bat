title @project.title@ 
java -jar @project.build.finalName@.@project.packaging@  -Xms1303m -Xmx1303m -XX:PermSize=256m -XX:MaxPermSize=256m > info.text

