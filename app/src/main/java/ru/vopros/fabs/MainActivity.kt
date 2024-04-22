package ru.vopros.fabs

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEachIndexed
import androidx.lifecycle.MutableLiveData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.vopros.fabs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private companion object {
        const val DURATION = 200L
    }

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())

    private val menuItems = MutableLiveData(
        listOf(
            R.id.fabDashboard,
            R.id.fabTime,
            R.id.fabSearch,
            R.id.fabHome,
            R.id.fabSettings,
        )
    )

    private val menuVisible = MutableLiveData(false)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.bottomToolBar)

        menuItems.observe(this) { fabList ->
            val fabViews = fabList.map { fabId ->
                findViewById<FloatingActionButton>(fabId)
            }.reversed()
            binding.menuList.removeAllViews()
            fabViews.forEach { fab ->
                fab.setOnTouchListener(FABTouchListener {
                    val newList = menuItems.value!!.toMutableList()
                    newList.removeIf { it == fab.id }
                    menuItems.value = newList
                })
                binding.menuList.addView(fab)
            }
        }
        menuVisible.observe(this) {
            val acceleration = 50L
            when (it) {
                true -> binding.menuList.forEachIndexed { i, fab ->
                    handler.postDelayed({ showFab(fab) }, acceleration * i.toLong())
                }
                else -> binding.menuList.forEachIndexed { i, fab ->
                    handler.postDelayed({ hideFab(fab) }, acceleration * i.toLong())
                }
            }
        }
    }

    private fun showFab(view: View) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.translationY = view.height.toFloat()

        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(DURATION)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    private fun hideFab(view: View) {
        view.animate()
            .translationY(view.height.toFloat())
            .setDuration(DURATION)
            .alpha(0f)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction { view.visibility = View.INVISIBLE }
            .start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toggleFabMenu -> {
                menuVisible.value = menuVisible.value?.not()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}