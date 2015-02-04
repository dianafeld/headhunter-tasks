git-task3
=========
mkdir task3  
cd task3  
git init

#### 1) Коммит содержищий в себе 1 файл: task3.txt содержащий любой 4х строчний текст - коммит меседж "commit <%username> 2" где <%username> -  ваш ник на github'е

echo "This  
is  
4-line  
file" | git hash-object -w --stdin  
git update-index --add --cacheinfo 100644 db519a42848e103615358633b47289e9a7d02022 task3.txt  
git write-tree  
echo "commit Meerstein 2" | git commit-tree 5aab93a345f424b64e1f9a4209ccfb74631799db  

#### 2) Коммит содержащий в себе 2 файла: task3_new.txt - содержащий 2 строчки любого текста и task3.txt содержащий любой 4х строчний текст (другой нежели в п.1) - коммит меседж "commit <%username> 2"

echo "2-line  
file" | git hash-object -w --stdin  
git update-index --add --cacheinfo 100644 31d8c907813a05f29425ea48d4e28fd449c3e34d task3_new.txt  

echo "Another  
file  
with 4  
lines" | git hash-object -w --stdin  
git update-index --add --cacheinfo 100644 499602bf6f3b96afe068d8d4f90e4aa7b7a87621 task3.txt  

git write-tree  
echo "commit Meerstein 2" | git commit-tree ef9946d80245e89ef8c912c77c5a72f1c8ee1284 -p b916d824c61a4373670af3bfe569c28dd02ba2b5

#### 1) Коммит содержищий в себе новую дирректорию gittask с файлом task3.txt первой версии - коммит меседж "commit <%username> 3"

git read-tree --prefix=gittask 5aab93a345f424b64e1f9a4209ccfb74631799db
git write-tree
echo "commit Meerstein 3" | git commit-tree c7cf816353ca2c27e784b028c387f22a1dfba2b1 -p 7a1f9b39a3605febd98ba46958afb0ad4067dc14

git update-ref refs/heads/master 0b1c5b33a03e0443794a9dcc1804f018c3465496

git remote add origin https://github.com/Meerstein/git-task3.git

git push origin master
