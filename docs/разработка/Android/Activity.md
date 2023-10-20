Launch Modes

- **Standard** - create a new instant of the activity in the task
- **SingleTop** - if the instance of the activity already exists at the top of the current task the system call `OnNewIntent()` for existing
  Activity, if not create a new one
- **SingleTask** - create a new task and instantiates the Activity at the root of new task. If Activity is exist in a separate task, call it
  and `OnNewIntent()`
- **SingleInstance** - same as `SignleTask` but does not laujnch any other activities into task

