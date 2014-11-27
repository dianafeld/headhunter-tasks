git-task2-command-log
=====================

#### 1. Должна содержать только frontik/testing.
git clone  https://github.com/hhru/frontik.git frontik1  
cd frontik1

git filter-branch --index-filter "git rm -r -f --cached --ignore-unmatch frontik/testing" --prune-empty
 
git remote rm origin  
git remote add origin https://github.com/Meerstein/git-task2-frontik1.git  
git push origin master


#### 2. Должна содержать всё кроме frontik/testing.

git clone  https://github.com/hhru/frontik.git frontik2  
cd frontik2  
git filter-branch --prune-empty --subdirectory-filter frontik/testing -- --all

git remote rm origin  
git remote add origin https://github.com/Meerstein/git-task2-frontik2.git  
git push origin --all
