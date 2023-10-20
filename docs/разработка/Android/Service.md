Виды:

- Started services
- Bounded services
- Foreground serivces

Внутренний процесс

```XML
process=":name"
```

JobIntentService это IntentService на стеройдах который захватывает wakelock (нужны [[Permission]] WAKE_LOCK, BIND_JOB_SERVICE)
если api >26 to Job
если api<26 то IntentService
