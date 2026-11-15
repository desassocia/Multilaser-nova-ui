package com.multilaser.novaui;

import android.app.*;
import android.content.*;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.text.DateFormat;
import java.util.*;

public class MainActivity extends Activity {
 private SharedPreferences p; private final List<ResolveInfo> apps=new ArrayList<>(), shown=new ArrayList<>();
 private LinearLayout root,body; private GridLayout grid; private EditText search;
 private String page="home",query=""; private float downX,downY;
 private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 private boolean dark(){return p.getBoolean("dark",true);}
 private boolean labels(){return p.getBoolean("labels",true);}
 private boolean anim(){return p.getBoolean("anim",true);}
 private int cols(){return p.getInt("cols",4);}
 private int iconSize(){return p.getInt("icons",52);}
 private int bg(){return dark()?Color.rgb(10,12,15):Color.rgb(244,247,250);}
 private int panel(){return dark()?Color.rgb(23,27,33):Color.WHITE;}
 private int fg(){return dark()?Color.rgb(246,248,250):Color.rgb(20,26,32);}
 private int muted(){return dark()?Color.rgb(153,164,176):Color.rgb(105,116,128);}
 private int accent(){switch(p.getInt("accent",0)){case 1:return Color.rgb(82,205,238);case 2:return Color.rgb(174,132,255);case 3:return Color.rgb(255,166,93);default:return Color.rgb(183,255,90);}}
 private GradientDrawable shape(int c,int r){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(dp(r));return d;}
 private LinearLayout col(){LinearLayout v=new LinearLayout(this);v.setOrientation(1);return v;}
 private LinearLayout row(){LinearLayout v=new LinearLayout(this);v.setGravity(Gravity.CENTER_VERTICAL);return v;}
 private TextView text(String s,int z,int c,boolean b){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);t.setGravity(Gravity.CENTER_VERTICAL);t.setTypeface(Typeface.create("sans-serif",b?1:0));t.setIncludeFontPadding(false);return t;}
 private String name(ResolveInfo a){try{return String.valueOf(a.loadLabel(getPackageManager()));}catch(Exception e){return a.activityInfo.packageName;}}
 @Override public void onCreate(Bundle b){super.onCreate(b);p=getSharedPreferences("nova_preferences",0);load();render();}
 private void load(){apps.clear();Intent i=new Intent(Intent.ACTION_MAIN);i.addCategory(Intent.CATEGORY_LAUNCHER);List<ResolveInfo> x=getPackageManager().queryIntentActivities(i,0);if(x!=null)apps.addAll(x);Collections.sort(apps,(a,b)->name(a).compareToIgnoreCase(name(b)));}
 private void render(){
  getWindow().setStatusBarColor(bg());getWindow().setNavigationBarColor(bg());
  getWindow().getDecorView().setSystemUiVisibility(dark()?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
  if(Build.VERSION.SDK_INT>=29){getWindow().setNavigationBarContrastEnforced(false);getWindow().setStatusBarContrastEnforced(false);}
  root=col();root.setPadding(dp(18),dp(18),dp(18),dp(8));root.setBackgroundColor(bg());
  LinearLayout head=row(),titles=col();titles.addView(text("MULTILASER  /  NOVA UI",10,accent(),true));
  TextView title=text(page.equals("apps")?"Aplicativos":page.equals("settings")?"Personalização":"Seu espaço.",27,fg(),true);
  LinearLayout.LayoutParams tl=new LinearLayout.LayoutParams(-1,dp(38));tl.topMargin=dp(5);titles.addView(title,tl);
  head.addView(titles,new LinearLayout.LayoutParams(0,-2,1));
  TextView refresh=text("↻",23,fg(),true);refresh.setGravity(Gravity.CENTER);refresh.setBackground(shape(panel(),15));refresh.setOnClickListener(v->{load();render();Toast.makeText(this,"Lista atualizada",0).show();});
  head.addView(refresh,new LinearLayout.LayoutParams(dp(46),dp(44)));root.addView(head);
  if(page.equals("settings"))settings();else buildAppsPage();
  root.addView(nav(),new LinearLayout.LayoutParams(-1,dp(58)));setContentView(root);
  if(anim()){root.setAlpha(.97f);root.animate().alpha(1).setDuration(130).setInterpolator(new DecelerateInterpolator()).start();}
 }
 private void buildAppsPage(){
  String sub=page.equals("home")?greeting():"Pesquise e organize os aplicativos instalados.";
  TextView subtitle=text(sub,12,muted(),false);LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(28));root.addView(subtitle,sp);
  search=new EditText(this);search.setSingleLine(true);search.setTextSize(14);search.setTextColor(fg());search.setHintTextColor(muted());search.setHint("⌕   Pesquisar aplicativos");search.setPadding(dp(15),0,dp(15),0);search.setBackground(shape(panel(),16));
  LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(-1,dp(50));ep.topMargin=dp(10);root.addView(search,ep);
  ScrollView scroll=new ScrollView(this);body=col();body.setPadding(0,dp(14),0,dp(10));
  if(page.equals("home"))homeTop();
  else body.addView(text("TODOS OS APLICATIVOS",10,muted(),true),new LinearLayout.LayoutParams(-1,dp(28)));
  grid=new GridLayout(this);grid.setColumnCount(cols());body.addView(grid);scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  updateGrid();search.setText(query);search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int f){}public void onTextChanged(CharSequence s,int a,int b,int c){query=s.toString();updateGrid();}public void afterTextChanged(android.text.Editable e){}});
  scroll.setOnTouchListener((v,e)->{if(e.getAction()==0){downX=e.getX();downY=e.getY();}if(e.getAction()==1){float dx=e.getX()-downX,dy=e.getY()-downY;if(Math.abs(dx)>dp(90)&&Math.abs(dx)>Math.abs(dy)*1.3f){setPage(dx<0?"apps":"home");return true;}}return false;});
 }
 private String greeting(){int h=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);return (h<12?"Bom dia":h<18?"Boa tarde":"Boa noite")+"  ·  "+DateFormat.getDateInstance(2,new Locale("pt","BR")).format(new Date());}
 private Set<String> fav(){return new HashSet<>(p.getStringSet("favorites",new HashSet<String>()));}
 private void homeTop(){
  body.addView(text("SEUS FAVORITOS",10,muted(),true),new LinearLayout.LayoutParams(-1,dp(25)));
  LinearLayout favorites=row();favorites.setGravity(Gravity.CENTER);favorites.setPadding(dp(4),dp(8),dp(4),dp(8));favorites.setBackground(shape(panel(),18));
  List<ResolveInfo> fs=new ArrayList<>();for(ResolveInfo a:apps)if(fav().contains(a.activityInfo.packageName))fs.add(a);
  if(fs.isEmpty()){TextView hint=text("Segure um app para adicionar aos favoritos",11,muted(),false);hint.setGravity(Gravity.CENTER);favorites.addView(hint,new LinearLayout.LayoutParams(-1,dp(65)));}
  else for(int i=0;i<Math.min(5,fs.size());i++)favorites.addView(tile(fs.get(i),true),new LinearLayout.LayoutParams(0,dp(80),1));
  LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,-2);fp.topMargin=dp(7);body.addView(favorites,fp);
  LinearLayout shortcuts=row();shortcuts.setPadding(0,dp(12),0,dp(8));
  shortcut(shortcuts,"Wi-Fi",Settings.ACTION_WIFI_SETTINGS);shortcut(shortcuts,"Bluetooth",Settings.ACTION_BLUETOOTH_SETTINGS);shortcut(shortcuts,"Tela",Settings.ACTION_DISPLAY_SETTINGS);body.addView(shortcuts);
  body.addView(text("SUGESTÕES",10,muted(),true),new LinearLayout.LayoutParams(-1,dp(32)));
 }
 private void shortcut(LinearLayout parent,String label,String action){TextView t=text(label,11,fg(),true);t.setGravity(Gravity.CENTER);t.setBackground(shape(panel(),14));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(42),1);lp.setMargins(dp(2),0,dp(2),0);parent.addView(t,lp);t.setOnClickListener(v->openSetting(action));}
 private void updateGrid(){
  if(grid==null)return;grid.removeAllViews();shown.clear();String q=query.trim().toLowerCase(Locale.ROOT);
  for(ResolveInfo a:apps)if(q.isEmpty()||name(a).toLowerCase(Locale.ROOT).contains(q))shown.add(a);
  if(page.equals("home")&&q.isEmpty()){Set<String> f=fav();Collections.sort(shown,(a,b)->{boolean x=f.contains(a.activityInfo.packageName),y=f.contains(b.activityInfo.packageName);return x==y?name(a).compareToIgnoreCase(name(b)):x?-1:1;});}
  int count=page.equals("home")&&q.isEmpty()?Math.min(12,shown.size()):shown.size();
  for(int i=0;i<count;i++){GridLayout.LayoutParams lp=new GridLayout.LayoutParams();lp.width=0;lp.height=dp(106);lp.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);lp.setMargins(dp(1),dp(2),dp(1),dp(2));grid.addView(tile(shown.get(i),false),lp);}
  if(shown.isEmpty()){TextView empty=text("Nenhum aplicativo encontrado.",13,muted(),false);empty.setGravity(Gravity.CENTER);grid.addView(empty,new ViewGroup.LayoutParams(-1,dp(80)));}
 }
 private LinearLayout tile(ResolveInfo a,boolean compact){
  LinearLayout t=col();t.setGravity(Gravity.CENTER);ImageView icon=new ImageView(this);try{icon.setImageDrawable(a.loadIcon(getPackageManager()));}catch(Exception e){icon.setImageResource(android.R.drawable.sym_def_app_icon);}
  int sz=dp(compact?40:iconSize());t.addView(icon,new LinearLayout.LayoutParams(sz,sz));
  if(labels()||compact){TextView label=text(name(a),compact?10:11,fg(),false);label.setGravity(Gravity.CENTER);label.setMaxLines(2);label.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(compact?27:34));lp.topMargin=dp(4);t.addView(label,lp);}
  t.setOnClickListener(v->launch(a));t.setOnLongClickListener(v->{appMenu(a);return true;});return t;
 }
 private void appMenu(ResolveInfo a){String pkg=a.activityInfo.packageName;Set<String> f=fav();boolean yes=f.contains(pkg);String[] opts={yes?"Remover dos favoritos":"Adicionar aos favoritos","Informações do aplicativo","Abrir"};
  new AlertDialog.Builder(this).setTitle(name(a)).setItems(opts,(d,w)->{if(w==0){if(yes)f.remove(pkg);else f.add(pkg);p.edit().putStringSet("favorites",f).apply();render();}else if(w==1)openSetting(Intent.ACTION_APPLICATION_DETAILS_SETTINGS,pkg);else launch(a);}).show();}
 private void launch(ResolveInfo a){try{Intent i=getPackageManager().getLaunchIntentForPackage(a.activityInfo.packageName);if(i!=null){i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);}}catch(Exception e){Toast.makeText(this,"Não foi possível abrir o aplicativo.",0).show();}}
 private void openSetting(String action){try{startActivity(new Intent(action));}catch(Exception e){Toast.makeText(this,"Atalho indisponível.",0).show();}}
 private void openSetting(String action,String pkg){try{startActivity(new Intent(action,Uri.parse("package:"+pkg)));}catch(Exception ignored){}}
 private LinearLayout nav(){LinearLayout bar=row();bar.setGravity(Gravity.CENTER);bar.setPadding(dp(4),dp(4),dp(4),dp(3));bar.setBackground(shape(panel(),18));navItem(bar,"⌂","Início","home");navItem(bar,"▦","Apps","apps");navItem(bar,"⚙","Ajustes","settings");return bar;}
 private void navItem(LinearLayout bar,String glyph,String label,String dest){boolean active=page.equals(dest);LinearLayout item=col();item.setGravity(Gravity.CENTER);TextView i=text(glyph,19,active?accent():muted(),true);i.setGravity(Gravity.CENTER);TextView l=text(label,10,active?fg():muted(),active);l.setGravity(Gravity.CENTER);item.addView(i);item.addView(l);item.setOnClickListener(v->setPage(dest));bar.addView(item,new LinearLayout.LayoutParams(0,-1,1));}
 private void setPage(String dest){try{((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(root.getWindowToken(),0);}catch(Exception e){}page=dest;query="";render();}
 private void settings(){
  ScrollView sc=new ScrollView(this);LinearLayout list=col();list.setPadding(0,dp(12),0,dp(10));
  setting(list,"Tema","Alternar entre claro e escuro",()->{p.edit().putBoolean("dark",!dark()).apply();render();});
  setting(list,"Cor de destaque","Verde, azul, roxo ou laranja",()->{p.edit().putInt("accent",(p.getInt("accent",0)+1)%4).apply();render();});
  setting(list,"Colunas","Grade atual: "+cols()+" · tocar para alterar",()->{p.edit().putInt("cols",cols()==5?3:cols()+1).apply();render();});
  setting(list,"Tamanho dos ícones",iconSize()+" dp · tocar para alterar",()->{p.edit().putInt("icons",iconSize()>=68?44:iconSize()+8).apply();render();});
  setting(list,"Nomes dos aplicativos",labels()?"Visíveis":"Ocultos",()->{p.edit().putBoolean("labels",!labels()).apply();render();});
  setting(list,"Animações",anim()?"Ativadas":"Desativadas",()->{p.edit().putBoolean("anim",!anim()).apply();render();});
  setting(list,"Papel de parede","Abrir configurações de papel de parede",()->openSetting(Intent.ACTION_SET_WALLPAPER));
  setting(list,"Tela e brilho","Configurações de exibição do Android",()->openSetting(Settings.ACTION_DISPLAY_SETTINGS));
  setting(list,"Configurações do sistema","Abrir ajustes do dispositivo",()->openSetting(Settings.ACTION_SETTINGS));
  setting(list,"Launcher padrão","Escolher o aplicativo de início",()->openSetting(Settings.ACTION_HOME_SETTINGS));
  TextView note=text("Os botões físicos/gestos de Início, Voltar e Recentes pertencem ao Android. A Nova UI oferece navegação própria dentro do launcher e ajusta as cores das barras do sistema quando permitido.",11,muted(),false);note.setPadding(dp(14),dp(14),dp(14),dp(14));note.setBackground(shape(panel(),15));list.addView(note);
  sc.addView(list);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
 }
 private void setting(LinearLayout list,String title,String desc,Runnable r){LinearLayout card=col();card.setPadding(dp(14),dp(13),dp(14),dp(13));card.setBackground(shape(panel(),15));card.addView(text(title,14,fg(),true));TextView d=text(desc,11,muted(),false);LinearLayout.LayoutParams dl=new LinearLayout.LayoutParams(-1,dp(22));dl.topMargin=dp(4);card.addView(d,dl);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(7);list.addView(card,lp);card.setOnClickListener(v->r.run());}
 @Override public void onBackPressed(){if(!query.isEmpty()){query="";if(search!=null)search.setText("");return;}if(!page.equals("home")){setPage("home");return;}super.onBackPressed();}
}