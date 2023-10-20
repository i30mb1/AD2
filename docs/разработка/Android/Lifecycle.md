Activity/Fragment lifecycle

Activity | Fragment| допы
--|--|--
onCreate()|-
-|onInflate()
-|onAttach()
-|onCreate()|если retainInstance то пропускается
-|onCreateView()
-|onViewCreated()
onContentChanged()|-
-|onActivityCreated()
-|onViewStateRestored()
onStart()|-
-|onStart()
onActivityResult()|-
onRestoreInstantState()|-
onPostCreate()|-
onResume()|-
-|onResume()
onPostResume()|-
onAttachToWindow()|-
onCreateOptionMenu()|-
-|onCreateOptionMenu()
onPrepareOptionMenu()|-
-|onPrepareOptionMenu()
onPause()|-
-|onPause()
onSaveInstantState()|onSaveInstantState()|может быть вызван в любое время до onDestroy()
onStop()| -
-|onStop()
-|onDestroyView()
onDestroy()|-
-|onDestroy()|если retainInstance то пропускается
-|onDetach()


