компонент [[Android]]
### При вызове диалога из Fragment

перед вызовом диалога нужно проверить Fragment.isStateSaved для предотвращения ошибки IllegalStateException (Can not perform this action
after onSavedState)
или вместо commit() использовать commitWithStateLoss()

### Жизненный Цикл

Before API 28
'''
LifecycleEvent.ON_STOP -> onSaveInstanceState() -> onStop()
'''
API 28+
'''
LifecycleEvent.ON_STOP -> onStop() -> onSaveInstanceState()
'''