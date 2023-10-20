- App-specific files (files removes on app unistall)
    - Internal
        - filesDir
        - cacheDir
    - external (need permission if API < 20)
        - externalFileDir
        - externalCacheDir
        - externalMediaDirs
- Non app-specific files (need permission)
    - `Enviroment.getDownloadCacheDirectory()`
    - `Enviroment.getRootDirectory()`
    - `Enviroment.getDataDirectory()`
- Media (need permission)
    - MediaStore API
- Documents
    - Storage Access Framework
- ScopeStorage

Extensions
`File.readText()`
`File.readBytes()`

проверка на external

- `if(Environment.getExternalStorageState == Environment.MEDIA_MOUNTED)`
