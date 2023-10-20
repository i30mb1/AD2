View держит hardReference на Activity в котором она созданна
Любой анонимный класс держит HardReference на свой класс в котором он был создан
RecyclerAdapter нужно занулять в Activity/Fragment
use lifecycleOwner instead of Context/Activity