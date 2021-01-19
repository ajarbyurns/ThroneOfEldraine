package my.mtg.throneofeldraine;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import my.mtg.throneofeldraine.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListAdapter extends ArrayAdapter<Card> {

    static class ViewHolder{
        ImageView image;
        TextView name;
        TextView mana;
        TextView type;
        TextView rarity;
        TextView text;
    }


    private int resourceLayout;
    private final Context mContext;
    private ArrayList<Card> cards; //list shown after and before filter
    private ArrayList<Card> arraylist; //contains the full list of cards
    private ArrayList<Card> temp = new ArrayList<>(); //2nd temporary storage
    private ArrayList<Card> temp2 = new ArrayList<>(); //3rd storage
    private final int height;
    private final int width;
    private Drawable drawable;
    private SpannableStringBuilder ssBuilder;
    private ImageSpan im;
    public ImageDialog imDialog;
    private String filter_name = "";
    private ArrayList<String> filter_mana = new ArrayList<>();
    private ArrayList<String> filter_rarity = new ArrayList<>();

    public ListAdapter(Context context, int resource, ArrayList<Card> items, int t_height, int t_width) {

        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.cards = items;

        arraylist = new ArrayList<>();
        arraylist.addAll(cards);

        this.height = t_height;
        this.width = t_width;

        imDialog = new ImageDialog(context);

        filter_mana.add("R");
        filter_mana.add("U");
        filter_mana.add("B");
        filter_mana.add("G");
        filter_mana.add("W");
        filter_mana.add("C");

        filter_rarity.add("C");
        filter_rarity.add("U");
        filter_rarity.add("R");
        filter_rarity.add("M");
    }

    @Override
    public int getCount(){
        return cards.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //using viewholder design pattern to save resources so the app doesn't call findviewbyid much
        ViewHolder holder;

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.listview_data_layout, parent, false);

            holder = new ViewHolder();

            holder.image = convertView.findViewById(R.id.image);
            holder.name = convertView.findViewById(R.id.name);
            holder.mana = convertView.findViewById(R.id.mana);
            holder.type = convertView.findViewById(R.id.type);
            holder.rarity = convertView.findViewById(R.id.rarity);
            holder.text = convertView.findViewById(R.id.text);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.image.setImageResource(cards.get(position).getImage());
        holder.image.setTag(cards.get(position).getImage());
        holder.name.setText(parseName(cards.get(position).getName(), filter_name));
        holder.mana.setText(parseText(cards.get(position).getMana()));

        //set card type + power/toughness
        String s = cards.get(position).getType();
        if(!cards.get(position).getPower().equals("")){
            s = s + " " + cards.get(position).getPower() + "/" + cards.get(position).getToughness();
        }
        holder.type.setText(s);

        //set the rarity symbol
        holder.rarity.setText(parseRarity(cards.get(position).getRarity()));

        //set card text
        String str = cards.get(position).getText();
        str = str.replaceAll("\n\\s+", "\n");
        holder.text.setText(parseText(str));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView temp = v.findViewById(R.id.image);
                int id = (int)temp.getTag();
                imDialog.imView.setImageResource(id);
                imDialog.show();
            }
        });

        return convertView;
    }

    //parse the name
    public SpannableStringBuilder parseName(String text, @NotNull String sub){

        int sublen = sub.length();

        ssBuilder = new SpannableStringBuilder(text);

        int index = text.toLowerCase().indexOf(sub.toLowerCase());

        if(sublen == 0 || index < 0) return ssBuilder;

        ssBuilder.setSpan(
                new ForegroundColorSpan(Color.RED), // Span to add
                index, // Start of the span (inclusive)
                index+sublen, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );

        return ssBuilder;
    }

    //parse the rarity text to image
    public SpannableStringBuilder parseRarity(String rarity){

        ssBuilder = new SpannableStringBuilder(rarity);

        switch(rarity) {
            case "C":
                drawable = mContext.getResources().getDrawable(R.drawable.common);break;
            case "U":
                drawable = mContext.getResources().getDrawable(R.drawable.uncommon);break;
            case "R":
                drawable = mContext.getResources().getDrawable(R.drawable.rare);break;
            case "M":
                drawable = mContext.getResources().getDrawable(R.drawable.mythic);break;
            default:
                break;
        }

        drawable.setBounds(0,0,width,height);
        im = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);

        ssBuilder.setSpan(
                im, // Span to add
                0, // Start of the span (inclusive)
                1, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );

        return ssBuilder;
    }

    //parse mana or ability text to image
    public SpannableStringBuilder parseText(String text){

        ssBuilder = new SpannableStringBuilder(text);

        if(!text.contains("{")) return ssBuilder;

        List<String> arr = new ArrayList<>();

        String[] split = text.split("(?<=\\})");
        for (String s : split) {
            String[] temp = s.split("(?=\\{)");
            arr.addAll(Arrays.asList(temp));
        }

        int index = 0;
        int end;

        for (String part: arr) {

            switch(part){
                //mana 0
                case "{0}": drawable = mContext.getResources().getDrawable(R.drawable.m0); end = index + 3;break;

                //mana 1
                case "{1}": drawable = mContext.getResources().getDrawable(R.drawable.m1); end = index + 3;break;

                //mana 2
                case "{2}": drawable = mContext.getResources().getDrawable(R.drawable.m2); end = index + 3;break;

                //mana 3
                case "{3}": drawable = mContext.getResources().getDrawable(R.drawable.m3); end = index + 3;break;

                //mana 4
                case "{4}": drawable = mContext.getResources().getDrawable(R.drawable.m4); end = index + 3;break;

                //mana 5
                case "{5}": drawable = mContext.getResources().getDrawable(R.drawable.m5); end = index + 3;break;

                //mana 6
                case "{6}": drawable = mContext.getResources().getDrawable(R.drawable.m6); end = index + 3;break;

                //mana 7
                case "{7}": drawable = mContext.getResources().getDrawable(R.drawable.m7); end = index + 3;break;

                //mana 8
                case "{8}": drawable = mContext.getResources().getDrawable(R.drawable.m8); end = index + 3;break;

                //mana 9
                case "{9}": drawable = mContext.getResources().getDrawable(R.drawable.m9); end = index + 3;break;

                //mana 10
                case "{10}": drawable = mContext.getResources().getDrawable(R.drawable.m10); end = index + 4;break;

                //mana x
                case "{X}": drawable = mContext.getResources().getDrawable(R.drawable.mx); end = index + 3;break;

                //mana R
                case "{R}": drawable = mContext.getResources().getDrawable(R.drawable.red); end = index + 3;break;

                //mana U
                case "{U}": drawable = mContext.getResources().getDrawable(R.drawable.blue); end = index + 3;break;

                //mana G
                case "{G}": drawable = mContext.getResources().getDrawable(R.drawable.green); end = index + 3;break;

                //mana B
                case"{B}": drawable = mContext.getResources().getDrawable(R.drawable.black); end = index + 3;break;

                //mana W
                case "{W}": drawable = mContext.getResources().getDrawable(R.drawable.white); end = index + 3;break;

                //mana azorius
                case "{W/U}": drawable = mContext.getResources().getDrawable(R.drawable.azorius); end = index + 5;break;

                //mana dimir
                case "{U/B}": drawable = mContext.getResources().getDrawable(R.drawable.dimir); end = index + 5;break;

                //mana golgari
                case "{B/G}": drawable = mContext.getResources().getDrawable(R.drawable.golgari); end = index + 5;break;

                //mana rakdos
                case "{B/R}": drawable = mContext.getResources().getDrawable(R.drawable.rakdos); end = index + 5;break;

                //mana izzet
                case "{U/R}": drawable = mContext.getResources().getDrawable(R.drawable.izzet); end = index + 5;break;

                //mana selesnya
                case "{G/W}": drawable = mContext.getResources().getDrawable(R.drawable.selesnya); end = index + 5;break;

                //mana orzhov
                case "{W/B}": drawable = mContext.getResources().getDrawable(R.drawable.orzhov); end = index + 5;break;

                //mana simic
                case "{G/U}": drawable = mContext.getResources().getDrawable(R.drawable.simic); end = index + 5;break;

                //mana gruul
                case "{R/G}": drawable = mContext.getResources().getDrawable(R.drawable.gruul); end = index + 5;break;

                //mana boros
                case "{R/W}": drawable = mContext.getResources().getDrawable(R.drawable.boros); end = index + 5;break;

                //tap
                case "{T}": drawable = mContext.getResources().getDrawable(R.drawable.tap); end = index + 3;break;

                default: end = index; break;
            }

            if(end > index) {
                drawable.setBounds(0, 0, width-10, height-10);
                im = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM);

                ssBuilder.setSpan(
                        im, // Span to add
                        index, // Start of the span (inclusive)
                        end, // End of the span (exclusive)
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
                );
            }

            index += part.length();
        }
        return ssBuilder;
    }

    public void filterName(String charText) {

        filter_name = charText.toLowerCase();

        filterAll();
    }

    public void filterMana(List<String> list) {

        filter_mana.clear();
        for(String s: list){
            switch(s){
                case "Red": filter_mana.add("R");break;
                case "Blue": filter_mana.add("U");break;
                case "White": filter_mana.add("W");break;
                case "Black": filter_mana.add("B");break;
                case "Green": filter_mana.add("G");break;
                case "Colorless": filter_mana.add("C");break;
                default: break;
            }
        }

        filterAll();
    }

    public void filterRarity(List<String> list) {

        filter_rarity.clear();
        for(String s: list){
            filter_rarity.add(""+s.charAt(0));
        }

        filterAll();
    }

    private void filterAll() {

        cards.clear();

        if(filter_mana.size() == 0 || filter_rarity.size() == 0){
            notifyDataSetChanged();
            return;
        }

        if (filter_mana.size() == 6 && filter_rarity.size() == 4 && filter_name.length() == 0) {

            cards.addAll(arraylist);

        } else {

            boolean colorless_flag = false;
            if(filter_mana.contains("C"))colorless_flag = true;

            temp2.clear();
            temp.clear();

            //filter by Mana first
            for (Card c : arraylist) {
                String mana = c.getMana();

                for (String symbol : filter_mana) {
                    if (mana.contains(symbol)) {
                        temp2.add(c);
                        break;
                    }
                }

                if (colorless_flag) {
                    if (!mana.contains("W") && !mana.contains("U") && !mana.contains("B") && !mana.contains("R") && !mana.contains("G")) {
                        temp2.add(c);
                    }
                }
            }

            //then filter by Rarity
            for (Card c : temp2) {
                String rarity = c.getRarity();
                for (String r : filter_rarity) {
                    if (rarity.equals(r)) {
                        temp.add(c);
                        break;
                    }
                }
            }

            //filter by text
            for (Card c : temp) {
                String name = c.getName().toLowerCase();
                if(name.contains(filter_name)){
                    cards.add(c);
                }
            }
        }

        notifyDataSetChanged();
    }
}

