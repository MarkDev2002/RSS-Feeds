package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity2 extends AppCompatActivity {

    TextView txtFoodName;
    ArrayList<Category> listfood;
    ArrayList<String> listTitle;
    ListView listSubFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Initial();
        setFoodName();
        readRss();

    }

    private void Initial() {
        txtFoodName = findViewById(R.id.txtFoodName);
        listSubFood = findViewById(R.id.listSubFood);
        listTitle = new ArrayList<>();

    }

    private void setFoodName() {
        Intent intent = getIntent();
        int i = intent.getIntExtra("numFood", 0);
        if (i == 0) {
            txtFoodName.setText("Proteins");
        } else if (i == 1) {
            txtFoodName.setText("Grains and Starches");
        } else if (i == 2) {
            txtFoodName.setText("Vitamins");
        } else if (i == 3) {
            txtFoodName.setText("Nutraceuticals");
        } else if (i == 4) {
            txtFoodName.setText("Fats and Oils");
        } else if (i == 5) {
            txtFoodName.setText("Amino Acids");
        } else if (i == 6) {
            txtFoodName.setText("Fibers and Legumes");
        } else if (i == 7) {
            txtFoodName.setText("Minerals");
        } else if (i == 8) {
            txtFoodName.setText("Processing functional ingredients");
        } else if (i == 9) {
            txtFoodName.setText("Preservatives");
        }
    }

    private String getUrl() {
        String url = "";
        Intent intent = getIntent();
        int i = intent.getIntExtra("numFood", 0);
        if (i == 0) {
            url = "https://www.petfoodindustry.com/rss/topic/292-proteins";
        } else if (i == 1) {
            url = "https://www.petfoodindustry.com/rss/topic/294-grains-and-starches";
        } else if (i == 2) {
            url = "https://www.petfoodindustry.com/rss/topic/296-vitamins";
        } else if (i == 3) {
            url = "https://www.petfoodindustry.com/rss/topic/298-nutraceuticals";
        } else if (i == 4) {
            url = "https://www.petfoodindustry.com/rss/topic/300-fats-and-oils";
        } else if (i == 5) {
            url = "https://www.petfoodindustry.com/rss/topic/293-amino-acids";
        } else if (i == 6) {
            url = "https://www.petfoodindustry.com/rss/topic/295-fibers-and-legumes";
        } else if (i == 7) {
            url = "https://www.petfoodindustry.com/rss/topic/297-minerals";
        } else if (i == 8) {
            url = "https://www.petfoodindustry.com/rss/topic/299-processing-functional-ingredients";
        } else if (i == 9) {
            url = "https://www.petfoodindustry.com/rss/topic/301-preservatives";
        }

        return url;
    }

    //XML Dom Parser
    protected String getNodeValue(String tag, Element element) {

        //tra ve mot cai NodeList (nodes) cua tat ca cac element con ( Element item ) theo name cua element
        NodeList nodeList = element.getElementsByTagName(tag);
        Node node = nodeList.item(0);
        if (node != null) { // neu node co content
            if (node.hasChildNodes()) { //neu node co chua node con ben trong no

                // duyet tung node con cua node elem cho den khi khong con node con (child ==null),
                // FirstChild la node con dau tien cua no
                Node child = node.getFirstChild();
                while (child != null) {
                    //neu node dang duyet la TEXT node hoặc CDATA (tuc la node chua text)
                    if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
                        return child.getNodeValue(); //trả về nội dung của text
                    }
                }
            }
        }
        return "";
    }

    public void readRss() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //khởi tạo đối tượng để mở kết nối
                HttpURLConnection urlConnection = null;

                try {
                    //lấy đường link
                    String getUrl = getUrl();
                    URL url = new URL(getUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    listfood = new ArrayList<>();

                    //đọc dữ liệu từ url
                    InputStream istream = urlConnection.getInputStream();

                    //XML DOM Parser
                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                    builderFactory.setNamespaceAware(true);
                    builderFactory.setCoalescing(true);

                    //tao doi tuong documentbuilder de co the lay duoc document tu XML va chuyen ve dang DOM de de doc
                    DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
                    Document doc = docBuilder.parse(istream);
                    NodeList nList = doc.getElementsByTagName("item");//lấy tất cả nội dung trong thẻ item
                    NodeList nListImg = doc.getElementsByTagName("media:content");//lấy hình ảnh trong thẻ media:content
                    String linkImg = "";

                    for (int i = 0; i < nList.getLength(); i++) {
                        if (nList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) nList.item(i); //lay ra mot element trong nodeList (chinh la 1 cai item)

                            for (int j = 0; j < nListImg.getLength(); j++) {
                                Node node = nListImg.item(j);
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    Element ele = (Element) node;
                                    if (j == i) {

                                        //trích xuất ra đối tượng
                                        linkImg = ele.getAttribute("url");
                                    }
                                }
                            }
                            Category food = new Category(getNodeValue("title", element),
                                    getNodeValue("description", element),
                                    getNodeValue("pubDate", element),
                                    getNodeValue("link", element),
                                    linkImg);
                            listfood.add(food);

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < listfood.size(); i++) {
                                Category food = listfood.get(i);
                                listTitle.add(food.getTitle());
                            }

                            ArrayAdapter adapter = new ArrayAdapter(MainActivity2.this, android.R.layout.simple_list_item_1, listTitle);
                            listSubFood.setAdapter(adapter);

                            listSubFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                //hộp thoại xuất hiện khi click vào danh mục rss
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Dialog dialog = new Dialog(MainActivity2.this);
                                    dialog.setContentView(R.layout.dialog_layout);
                                    TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                                    TextView txtDis = dialog.findViewById(R.id.txtDis);
                                    TextView txtImg = dialog.findViewById(R.id.txtImg);
                                    TextView txtDate = dialog.findViewById(R.id.txtDate);
                                    ImageView img = dialog.findViewById(R.id.imgFood);
                                    Button btnMore = dialog.findViewById(R.id.btnMore);
                                    Button btnClose = dialog.findViewById(R.id.btnClose);

                                    Category food = listfood.get(i);
                                    txtTitle.setText(food.getTitle());
                                    txtDis.setText(Html.fromHtml(food.getDescription()));
                                    txtImg.setText(food.getTitle());
                                    txtDate.setText(food.getDate());
                                    Picasso.with(dialog.getContext())
                                            .load(food.getImg())
                                            .into(img); //Picasso de load anh tu mang ve may ao

                                    //truy cập link đường dẫn
                                    btnMore.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Uri uri = Uri.parse(food.getLink());
                                            Intent web = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(web);
                                        }
                                    });

                                    //đóng dialog mời truy cập
                                    btnClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.hide();
                                        }
                                    });
                                    dialog.show();
                                }
                            });

                        }
                    });

                }

            }
        }).start();
    }
}