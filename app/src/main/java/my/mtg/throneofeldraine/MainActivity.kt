package my.mtg.throneofeldraine

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    lateinit var listAdapter: ListAdapter

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "MTG Eldraine"

        val parser = XMLParser()
        parser.mContext = this
        val cards = parser.parse(assets.open("cards.xml"))
        listAdapter = ListAdapter(
            this,
            R.layout.listview_data_layout,
            cards,
            35,
            35
        )
        val list = findViewById<ListView>(R.id.listView)
        list.adapter = listAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_layout, menu)
        val searchViewItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView = searchViewItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(str: String): Boolean {
                listAdapter.filterName(str)
                return false
            }
        })

        val manaSpinnerItem: MenuItem = menu.findItem(R.id.mana_spinner)
        val manaSpinner = manaSpinnerItem.actionView as MultiSelectionSpinner
        val manaItems = arrayOf(
            SpannableStringBuilder("White  "),
            SpannableStringBuilder("Black  "),
            SpannableStringBuilder("Blue  "),
            SpannableStringBuilder("Red  "),
            SpannableStringBuilder("Green  "),
            SpannableStringBuilder("Colorless  "))

        val colors = arrayOf(
            R.drawable.white,
            R.drawable.black,
            R.drawable.blue,
            R.drawable.red,
            R.drawable.green,
            R.drawable.colorless
        )
        for(i in 0..5){
            val drawable = ContextCompat.getDrawable(this, colors[i])
            drawable?.setBounds(0, 0, 35, 35)
            val im = drawable?.let { ImageSpan(it, DynamicDrawableSpan.ALIGN_BASELINE) }
            manaItems[i].setSpan(im, manaItems[i].length-1, manaItems[i].length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        manaSpinner.setItems(manaItems)
        manaSpinner.builder.setCancelable(true)
        manaSpinner.builder.setOnCancelListener { listAdapter.filterMana(manaSpinner.selectedStrings) }

        val raritySpinnerItem: MenuItem = menu.findItem(R.id.rarity_spinner)
        val raritySpinner = raritySpinnerItem.actionView as MultiSelectionSpinner
        val rarityItems = arrayOf(
            SpannableStringBuilder("Common  "),
            SpannableStringBuilder("Uncommon  "),
            SpannableStringBuilder("Rare  "),
            SpannableStringBuilder("Mythic  "))

        val rarities = arrayOf(
            R.drawable.common,
            R.drawable.uncommon,
            R.drawable.rare,
            R.drawable.mythic
        )
        for(i in 0..3){
            val drawable = ContextCompat.getDrawable(this, rarities[i])
            drawable?.setBounds(0, 0, 35, 35)
            val im = drawable?.let { ImageSpan(it, DynamicDrawableSpan.ALIGN_BASELINE) }
            rarityItems[i].setSpan(im, rarityItems[i].length-1, rarityItems[i].length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        raritySpinner.setItems(rarityItems)
        raritySpinner.builder.setCancelable(true)
        raritySpinner.builder.setOnCancelListener { listAdapter.filterRarity(raritySpinner.selectedStrings) }
        return true
    }

}
