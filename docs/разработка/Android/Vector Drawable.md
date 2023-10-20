vector.xml -> inflate -> Vector Drawable -> Draw -> Bitmap

```XML
<vector
		android:width="24dp" - физический размер
		android:viewportWidth="24dp" - virtual canvas
```

```XML
<path
	  android:pathData="..." - основной компонент
```

M0,0 - move to
L24,0 - draw line to
C24,24 24,0 0,0 - curve to
Z - close
m,l,c,z - маленькие буквы копируют пред. координаты и изменяют их