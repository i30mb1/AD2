import androidx.fragment.app.Fragment

//package n7.ad2.games.internal.games
//
//import android.R
//import android.animation.ArgbEvaluator
//import android.media.SoundPool
//import android.os.Handler
//import android.util.TypedValue
//import android.view.View
//import android.widget.TextView
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import androidx.transition.ChangeBounds
//import androidx.transition.Fade
//import androidx.transition.TransitionManager
//import androidx.transition.TransitionSet
//import org.json.JSONArray
//import org.json.JSONObject
//import java.io.IOException
//import java.util.LinkedList
//import java.util.Random
//import java.util.concurrent.Executor
//import java.util.concurrent.Executors
//
class Game1p1 : Fragment() {
//    var secondRemaining: Int = 30
//    var isLoading: Boolean = true
//    var slot1: String = ""
//    var slot2: String = ""
//    var slot3: String = ""
//    var slot4: String = ""
//    var currentSpellLVL: Int = 1
//    var score: Int = 0
//    var lock: Boolean = true
//    var endText: String = ""
//    private var rightAnswerString: String? = null
//    private var diskIO: Executor? = null
//    private var hero = 0
//    private var binding: ActivityGame1p1Binding? = null
//    private var soundPool: SoundPool? = null
//    private var soundYes = 0
//    private var soundNo = 0
//    private var you_are_succeeded_where_other_did_not = 0
//    private var you_are_the_most_successful = 0
//    private var does_this_unit_have_a_soul = 0
//    private var yes_it_does = 0
//    protected override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        diskIO = Executors.newSingleThreadExecutor()
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_game1p1)
//        binding.setActivity(this)
//        binding.executePendingBindings()
//        loadPlayer()
//        startCountDownTimer()
//        startGame()
//    }
//
//    private fun loadPlayer() {
//        soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
//        soundYes = soundPool.load(this, R.raw.yes, 1)
//        soundNo = soundPool.load(this, R.raw.no, 1)
//        you_are_succeeded_where_other_did_not = soundPool.load(this, R.raw.you_are_succeeded_where_other_did_not, 1)
//        you_are_the_most_successful = soundPool.load(this, R.raw.you_are_the_most_successful, 1)
//        does_this_unit_have_a_soul = soundPool.load(this, R.raw.does_this_unit_have_a_soul, 1)
//        yes_it_does = soundPool.load(this, R.raw.yes_it_does, 1)
//    }
//
//    private fun startCountDownTimer() {
//        object : CountDownTimer(30000, 100) {
//            override fun onTick(millisUntilFinished: Long) {
//                secondRemaining.set((millisUntilFinished / 1000).toInt())
//            }
//
//            override fun onFinish() {
//                secondRemaining.set(0)
//                showResult()
//            }
//        }.start()
//    }
//
//    private fun showResult() {
//        lock.set(true)
//        isLoading.set(false)
//        setEndText()
//        changeBackgroundColor()
//        startConstraintAnimation()
//    }
//
//    private fun startConstraintAnimation() {
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(this, R.layout.activity_game1p1_finish)
//        val transitionSet = TransitionSet()
//            .addTransition(ChangeBounds().setInterpolator(LinearInterpolator())
//                .addTarget(binding.linearLayout)
//                .addTarget(binding.tvActivityGame1Timer)
//                .addTarget(binding.tvActivityGame1Score)
//                .setDuration(1000))
//            .addTransition(Fade(Fade.OUT).setDuration(500)
//                .addTarget(binding.ivActivityGame1P1Spell)
//                .addTarget(binding.linearLayout2))
//            .addTransition(ChangeBounds().setInterpolator(BounceInterpolator())
//                .addTarget(binding.textView)
//                .setStartDelay(1000)
//                .setDuration(1000))
//        TransitionManager.beginDelayedTransition(binding.getRoot() as ViewGroup, transitionSet)
//        constraintSet.applyTo(binding.getRoot() as ConstraintLayout)
//        Handler().postDelayed({ finish() }, 6000)
//    }
//
//    private fun changeBackgroundColor() {
//        val colorAnimation: ValueAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorAccentTheme, getResources().getColor(R.color.black))
//        colorAnimation.setDuration(1000)
//        colorAnimation.addUpdateListener(object : AnimatorUpdateListener {
//            override fun onAnimationUpdate(animator: ValueAnimator) {
//                binding.getRoot().setBackgroundColor(animator.getAnimatedValue() as Int)
//            }
//        })
//        colorAnimation.start()
//    }
//
//    private val colorAccentTheme: Int
//        private get() {
//            val typedValue = TypedValue()
//            getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true)
//            return typedValue.data
//        }
//
//    private fun startGame() {
//        diskIO!!.execute {
//            try {
//                isLoading.set(true)
//                val listHeroes: Array<String> = getAssets().list("heroes")
//                hero = Random().nextInt(listHeroes.size)
//                val spells: JSONArray = JSONObject(Utils.readJSONFromAsset(this@Game1p1, "heroes/" + listHeroes[hero] + "/" + "ru_abilities.json")).getJSONArray("abilities")
//                val randomSpell = Random().nextInt(spells.length())
//                //загружаем ману для способности и уровень способности
//                val spellManaCostString = spells.getJSONObject(randomSpell)["mana"].toString()
//                if (spellManaCostString == "0" || spellManaCostString.contains("(") || spellManaCostString.contains("+") || spellManaCostString.contains("%")) {
//                    throw IOException()
//                }
//                val spellLVL = Random().nextInt(spellManaCostString.split("/").toTypedArray().size)
//                val spellManaPoints = spellManaCostString.split("/").toTypedArray()[spellLVL]
//                setLVL(spellLVL)
//                setMp(spellManaPoints)
//                setImageSpell(String.format("file:///android_asset/heroes/%s/%s.webp", listHeroes[hero], (randomSpell + 1).toString()))
//            } catch (e: IOException) {
//                startGame()
//            } catch (e: JSONException) {
//                startGame()
//            } finally {
//                isLoading.set(false)
//                lock.set(false)
//            }
//        }
//    }
//
//    private fun setImageSpell(format: String) {
//        runOnUiThread(Runnable { })
//    }
//
//    private fun setMp(manaPoints: String) {
//        diskIO!!.execute {
//            val numbersDiff = intArrayOf(-30, -25, -20, -15, -10, -5, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50)
//            rightAnswerString = manaPoints
//            val rightAnswerInt = Integer.valueOf(manaPoints)
//            val listWithValues = LinkedList<Int>()
//            listWithValues.add(rightAnswerInt)
//            do {
//                val minus = rightAnswerInt + numbersDiff[Random().nextInt(numbersDiff.size)]
//                if (minus > 0) listWithValues.add(minus)
//            } while (listWithValues.size != 4)
//            slot1.set("" + listWithValues.removeAt(Random().nextInt(listWithValues.size)))
//            slot2.set("" + listWithValues.removeAt(Random().nextInt(listWithValues.size)))
//            slot3.set("" + listWithValues.removeAt(Random().nextInt(listWithValues.size)))
//            slot4.set("" + listWithValues.removeAt(Random().nextInt(listWithValues.size)))
//        }
//    }
//
//    private fun setLVL(i: Int) {
//        currentSpellLVL.set(i)
//    }
//
//    fun expandAnimation(view: View) {
//        val animationSet = AnimationSet(false)
//        val alphaAnimation = AlphaAnimation(1f, 0f)
//        val scaleAnimation = ScaleAnimation(1f, 3f, 1f, 3f, view.width / 2, view.height / 2)
//        animationSet.addAnimation(scaleAnimation)
//        animationSet.addAnimation(alphaAnimation)
//        animationSet.setDuration(200)
//        view.startAnimation(animationSet)
//    }
//
//    fun check(view: View) {
//        lock.set(true)
//        expandAnimation(view)
//        val isRight: Boolean
//        if ((view as TextView).text == rightAnswerString) {
//            view.setTextColor(getResources().getColor(R.color.holo_green_light))
//            isRight = true
//            score.set(score.get() + 1)
//        } else {
//            view.setTextColor(getResources().getColor(R.color.holo_red_light))
//            score.set(score.get() - 1)
//            isRight = false
//        }
//        playSound(isRight)
//        Handler().postDelayed({ startGame() }, 300)
//    }
//
//    private fun playSound(isRight: Boolean) {
//        if (isRight) {
//            soundPool.play(soundYes, 0.6f, 0.6f, 0, 0, 1f)
//        } else {
//            soundPool.play(soundNo, 0.6f, 0.6f, 0, 0, 1f)
//        }
//    }
//
//    private fun setEndText() {
//        when (score.get()) {
//            -6 -> endText.set("SUCK MY DATA, HUMAN!")
//            -5 -> endText.set("If our lives are already written, it would take a courageous man to change the script.")
//            -4 -> endText.set("Stand in the ashes of a trillion dead souls and ask the ghosts if honor matters. Their silence is your answer.")
//            -3 -> endText.set("You are great in bed ;) …. You can sleep for days.")
//            -2 -> endText.set("You didn’t fall. The floor just needed a hug.")
//            -1 -> endText.set("SKELETON_KING PLAYER DETECTED.")
//            0, 1 -> endText.set("GG&WP!")
//            2 -> endText.set("Are you a boy or a girl?")
//            3 -> {
//                endText.set("sorry, you lose!")
//                endText.set("you do not get older. you lvl up.")
//            }
//            4 -> endText.set("you do not get older. you lvl up.")
//            5 -> {
//                endText.set("DOES THIS UNIT HAVE A SOUL?")
//                soundPool.play(does_this_unit_have_a_soul, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            6 -> endText.set("A hero need not speak. When he is gone, the world will speak for him.")
//            7 -> {
//                endText.set("UGANDA FOREVER!")
//                soundPool.play(you_are_the_most_successful, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            8 -> endText.set("You are nobody.. nobody's perfect.. therefore YOU ARE PERFECT!")
//            9 -> endText.set("If anything’s possible, then is it possible that nothing’s possible?")
//            10 -> {
//                endText.set("YOU SUCCEEDED WHERE OTHER DO NOT.")
//                soundPool.play(you_are_succeeded_where_other_did_not, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            else -> endText.set("GG&WP!")
//        }
//    }
}