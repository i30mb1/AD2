Анимации в Android

- Property Animation - изменения свойств View
    - ValueAnimator - предназначем для плавного изменения значения с течением времени
        - `.ofInt(...)`
        - `.ofFloat(700f)`
        - `.ofArgb(...)`
        - любой другой тип можно реализовать наследуя TypeEvaluator
    - ObjectAnimator - наследник ValueAnimator, предназначем для плавного изменения свойст View, имеет доступ к текущему значению
      анимируемого свойства
        - `.ofInt(...)`
        - `.ofPropertyValuesHolder` - позволяет анимировать несколько свойств
        - AnimatorSet - позволяет групировать аниматоры между собой
    - ViewPropertyAnimation
        - `.animate().x(60f).start()`
        - Batch операции анимации, тики анимации будут выполнятся за 1 такт
        - Выполняются только на RenderNode, кэшируются и лишний раз onDraw не вызывается
- Кадровая анимации из PNG... файлов
    - `AnimationDrawable`
- View Animation - изменяет только представление и только 1 свойство
    - `TranslateAnimation`
    - `RotateAnimation`
    - `AlphaAnimation`
- LayoutAnimation - для ViewGroup
    - `android:animateLayoutChanges=true`
- TransitionManager - анимирует View в Constraint
- Animated Vector Drawable
