package my.mtg.throneofeldraine

import android.content.Context
import android.content.res.Resources
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

class XMLParser {

    private val cards = ArrayList<Card>()
    private var card: Card =
        Card("", 0, "", "", "", "", "", "")
    private var text: String? = null
    var mContext: Context? = null
    private var mResources: Resources? = null

    fun parse(inputStream: InputStream): ArrayList<Card> {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            mResources = mContext!!.resources
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals("cards", ignoreCase = false)) {
                        card = Card(
                            "",
                            0,
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (tagname.equals("cards", ignoreCase = false)) {
                        cards.add(
                            Card(
                                card.name,
                                card.image,
                                card.mana,
                                card.type,
                                card.rarity,
                                card.text,
                                card.power,
                                card.toughness
                            )
                        )
                    } else if (tagname.equals("name", ignoreCase = true)) {
                        card.name = text!!
                        val str = text!!.replace("[^A-Za-z0-9 ]".toRegex(), "").replace("\\s".toRegex(), "").toLowerCase()
                        card.image = mResources!!.getIdentifier(str, "drawable", mContext!!.packageName)
                    } else if (tagname.equals("manaCost", ignoreCase = true)) {
                        card.mana = text!!
                    } else if (tagname.equals("type", ignoreCase = true)) {
                        card.type = text!!
                    } else if (tagname.equals("rarity", ignoreCase = true)) {
                        card.rarity = text!!.capitalize().first().toString()
                    } else if (tagname.equals("text", ignoreCase = true)) {
                        card.text = text!!
                    } else if (tagname.equals("power", ignoreCase = true)) {
                        card.power = text!!
                    } else if (tagname.equals("toughness", ignoreCase = true)) {
                        card.toughness = text!!
                    } else -> {

                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cards
    }
}