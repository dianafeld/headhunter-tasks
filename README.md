git-task1-command-log
=====================

#### 01. create repo (repo A)
mkdir repoA  
cd repoA  
git init

#### 02. repo A: create 1st commit with some files (file A, file B)
echo "This is file A" > fileA.txt  
echo "This is file B" > fileB.txt  
git add fileA.txt fileB.txt  
git commit -m 'new files: fileA, fileB'  

#### 03. repo A: create branch branch1 from master
git checkout -b 'branch1'

#### 04. clone the current repo (repo B)
cd ..  
git clone repoA repoB  
cd repoB

#### 05. repo B, branch1: create 2nd commit containing new file (file C)
echo -n "This is file C" > fileC.txt  
git add fileC.txt  
git commit -m 'new file: fileC'

#### 06. repo B: push changes to repo A
cd ../repoA  
git checkout master  
cd ../repoB  
git push origin

#### 07. repo A, branch1: modify line#1 in file C and commit
cd ../repoA  
git checkout branch1  
echo "A" >> fileC.txt  
git add fileC.txt  
git commit -m 'modified: fileC in A'

#### 08. repo B, branch1: modify line#1 in file C and commit
cd ../repoB  
echo "B" >> fileC.txt  
git add fileC.txt  
git commit -m 'modified: fileC in B'

#### 09. repo B, branch1: fetch changes from repo A
git fetch origin

#### 10. repo B, branch1: merge in repoA's branch1 (resolve conflict)
git merge origin/branch1  
git checkout --theirs fileC.txt  
git add fileC.txt  
git commit -m 'merge fileC in B from A'

#### 11. repo A: add repoB as new remote
cd ../repoA  
git remote add repoB ../repoB

#### 12. repo A, branch1: merge in repoB's branch1
git pull repoB branch1

#### Push to github
git remote add github https://github.com/Meerstein/repoA.git  
git push github --all  
cd ../repoB  
git remote add github https://github.com/Meerstein/repoB.git  
git push github --all
