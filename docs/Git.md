- разбивать коммиты на маленькие цельные рабочие куски кода
- HEAD^ == HEAD~1

При работе в консоле придется иметь дело с [[VIM]]

Команды

- git reset HEAD^ - undo last commit but keep the changes
- git reset --hard HEAD^ - undo last commit
- git merge --squash **branch7** - apply the changes from **branch7** without merging commits
- git commit --amend --no-edit - adding changes to the last commit
- git push -f origin branch7 - pushing an amended commit
- git rm --cached path/to/file - remove file from indexing

