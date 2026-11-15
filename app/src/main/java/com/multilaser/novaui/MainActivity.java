package com.multilaser.novaui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    private static final String PREFS = "nova_preferences";
    private final List<ResolveInfo> allApps = new ArrayList<>();
    private LinearLayout page;
    private EditText searchInput;
    private boolean drawerOpen = false;
    private String currentQuery = "";

    private int bg() { return isDark() ? Color.rgb(9, 11, 14) : Color.rgb(242, 245, 248); }
    private int panel() { return isDark() ? Color.rgb(22, 26, 31) : Color.WHITE; }
    private int primaryText() { return isDark() ? Color.rgb(245, 247, 250) : Color.rgb(22, 27, 32); }
    private int secondaryText() { return isDark() ? Color.rgb(157, 166, 177) : Color.rgb(105, 115, 126); }
    private int accent() {
        int choice = getPreferences(0).getInt("accent", 0);
        if (choice == 1) return Color.rgb(80, 210, 235);
        if (choice == 2) return Color.rgb(174, 133, 255);
        return Color.rgb(183, 255, 90);
    }
    private boolean isDark() { return getPreferences(0).getBoolean("dark", true); }
    private int columns() { return getPreferences(0).getInt("columns", 4); }
    private int iconSize() { return getPreferences(0).getInt("icon_size", 54); }
    private boolean showLabels() { return getPreferences(0).getBoolean("labels", true); }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        Window window = getWindow();
        window.setStatusBarColor(isDark() ? Color.rgb(9,11,14) : Color.rgb(242,245,248));
        window.setNavigationBarColor(isDark() ? Color.rgb(9,11,14) : Color.rgb(242,245,248));
        loadApps();
        renderHome();
    }

    private void loadApps() {
        allApps.clear();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> found = getPackageManager().queryIntentActivities(intent, 0);
        if (found != null) allApps.addAll(found);
        Collections.sort(allApps, new Comparator<ResolveInfo>() {
            @Override public int compare(ResolveInfo a, ResolveInfo b) {
                String left = String.valueOf(a.loadLabel(getPackageManager()));
                String right = String.valueOf(b.loadLabel(getPackageManager()));
                return left.compareToIgnoreCase(right);
            }
        });
    }

    private int dp(float value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setFontFeatureSettings("kern");
        if (bold) view.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        else view.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        return view;
    }

    private TextView pill(String value, Runnable action, boolean selected) {
        TextView view = text(value, 13, selected ? Color.rgb(9, 11, 14) : primaryText(), true);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(16), dp(11), dp(16), dp(11));
        view.setBackground(shape(selected ? accent() : panel(), 22));
        view.setOnClickListener(v -> action.run());
        return view;
    }

    private void renderHome() {
        page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(22), dp(20), dp(12));
        page.setBackgroundColor(bg());

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        TextView eyebrow = text("MULTILASER  /  NOVA UI", 10, accent(), true);
        TextView title = text(drawerOpen ? "Seus aplicativos" : "Seu espaço.", 28, primaryText(), true);
        titles.addView(eyebrow);
        titles.addView(title);
        header.addView(titles, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        TextView settings = pill("⚙", this::showSettings, false);
        header.addView(settings, new LinearLayout.LayoutParams(dp(48), dp(44)));
        page.addView(header);

        TextView subtitle = text(drawerOpen ? "Tudo o que está instalado, em um só lugar." : "Uma experiência mais leve. Do seu jeito.", 13, secondaryText(), false);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(-1, dp(30));
        subParams.topMargin = dp(3);
        page.addView(subtitle, subParams);

        searchInput = new EditText(this);
        searchInput.setSingleLine(true);
        searchInput.setTextSize(14);
        searchInput.setTextColor(primaryText());
        searchInput.setHintTextColor(secondaryText());
        searchInput.setHint("Pesquisar aplicativos");
        searchInput.setPadding(dp(16), 0, dp(16), 0);
        searchInput.setBackground(shape(panel(), 17));
        searchInput.setText(currentQuery);
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, dp(52));
        searchParams.topMargin = dp(18);
        page.addView(searchInput, searchParams);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(18), 0, dp(12));

        if (!drawerOpen && currentQuery.isEmpty()) {
            TextView quickLabel = text("ACESSO RÁPIDO", 11, secondaryText(), true);
            content.addView(quickLabel);
            LinearLayout dock = new LinearLayout(this);
            dock.setOrientation(LinearLayout.HORIZONTAL);
            dock.setGravity(Gravity.CENTER);
            dock.setPadding(dp(8), dp(12), dp(8), dp(12));
            dock.setBackground(shape(panel(), 22));
            int count = Math.min(4, allApps.size());
            for (int i = 0; i < count; i++) {
                final ResolveInfo app = allApps.get(i);
                LinearLayout tile = appTile(app, true);
                dock.addView(tile, new LinearLayout.LayoutParams(0, dp(88), 1));
            }
            LinearLayout.LayoutParams dockParams = new LinearLayout.LayoutParams(-1, -2);
            dockParams.topMargin = dp(10);
            content.addView(dock, dockParams);
            TextView appHeading = text("APLICATIVOS", 11, secondaryText(), true);
            LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(-1, dp(42));
            headingParams.topMargin = dp(12);
            content.addView(appHeading, headingParams);
        } else {
            TextView appHeading = text("RESULTADOS  ·  " + filteredApps().size(), 11, secondaryText(), true);
            content.addView(appHeading);
        }

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(columns());
        grid.setUseDefaultMargins(false);
        List<ResolveInfo> visible = filteredApps();
        int limit = (!drawerOpen && currentQuery.isEmpty()) ? Math.min(12, visible.size()) : visible.size();
        for (int i = 0; i < limit; i++) {
            ResolveInfo app = visible.get(i);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = dp(112);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(dp(2), dp(3), dp(2), dp(3));
            grid.addView(appTile(app, false), params);
        }
        content.addView(grid);
        if (visible.isEmpty()) {
            TextView empty = text("Nenhum aplicativo encontrado.", 14, secondaryText(), false);
            empty.setGravity(Gravity.CENTER);
            content.addView(empty, new LinearLayout.LayoutParams(-1, dp(100)));
        }
        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        TextView home = pill(drawerOpen ? "⌂  Início" : "▦  Todos os apps", () -> {
            drawerOpen = !drawerOpen;
            currentQuery = "";
            renderHome();
        }, true);
        TextView customize = pill("Personalizar", this::showSettings, false);
        LinearLayout.LayoutParams bottomItem = new LinearLayout.LayoutParams(0, dp(48), 1);
        bottomItem.setMargins(dp(4), dp(6), dp(4), dp(4));
        bottom.addView(home, bottomItem);
        bottom.addView(customize, bottomItem);
        page.addView(bottom);

        setContentView(page);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                int position = searchInput.getSelectionStart();
                renderHome();
                if (searchInput != null) {
                    searchInput.requestFocus();
                    searchInput.setSelection(Math.min(position, searchInput.length()));
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private List<ResolveInfo> filteredApps() {
        List<ResolveInfo> result = new ArrayList<>();
        String query = currentQuery.trim().toLowerCase();
        for (ResolveInfo app : allApps) {
            String label = String.valueOf(app.loadLabel(getPackageManager())).toLowerCase();
            if (query.isEmpty() || label.contains(query)) result.add(app);
        }
        return result;
    }

    private LinearLayout appTile(ResolveInfo app, boolean compact) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(3), dp(5), dp(3), dp(5));
        ImageView icon = new ImageView(this);
        try { icon.setImageDrawable(app.loadIcon(getPackageManager())); }
        catch (Exception ignored) { icon.setImageResource(android.R.drawable.sym_def_app_icon); }
        int size = compact ? dp(42) : dp(iconSize());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
        tile.addView(icon, iconParams);
        if (!compact && showLabels()) {
            TextView label = text(String.valueOf(app.loadLabel(getPackageManager())), 11, primaryText(), false);
            label.setGravity(Gravity.CENTER);
            label.setMaxLines(2);
            label.setPadding(dp(2), dp(5), dp(2), 0);
            tile.addView(label, new LinearLayout.LayoutParams(-1, dp(34)));
        } else if (compact) {
            TextView label = text(String.valueOf(app.loadLabel(getPackageManager())), 10, primaryText(), false);
            label.setGravity(Gravity.CENTER);
            label.setMaxLines(1);
            tile.addView(label, new LinearLayout.LayoutParams(-1, dp(25)));
        }
        tile.setOnClickListener(v -> launch(app));
        tile.setOnLongClickListener(v -> {
            Intent info = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + app.activityInfo.packageName));
            try { startActivity(info); } catch (Exception ignored) {}
            return true;
        });
        return tile;
    }

    private void launch(ResolveInfo app) {
        try {
            Intent launch = getPackageManager().getLaunchIntentForPackage(app.activityInfo.packageName);
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launch);
            }
        } catch (Exception ignored) {
            new AlertDialog.Builder(this).setMessage("Não foi possível abrir este aplicativo.")
                    .setPositiveButton("OK", null).show();
        }
    }

    private void showSettings() {
        String[] options = {
                "Tema: " + (isDark() ? "Escuro" : "Claro"),
                "Cor de destaque",
                "Colunas: " + columns(),
                "Tamanho dos ícones: " + iconSize() + " dp",
                "Nomes dos aplicativos: " + (showLabels() ? "Visíveis" : "Ocultos"),
                "Atualizar lista de aplicativos"
        };
        new AlertDialog.Builder(this)
                .setTitle("Personalizar Nova UI")
                .setItems(options, (dialog, which) -> {
                    android.content.SharedPreferences.Editor edit = getPreferences(0).edit();
                    if (which == 0) edit.putBoolean("dark", !isDark()).apply();
                    if (which == 1) edit.putInt("accent", (getPreferences(0).getInt("accent", 0) + 1) % 3).apply();
                    if (which == 2) edit.putInt("columns", columns() == 5 ? 3 : columns() + 1).apply();
                    if (which == 3) edit.putInt("icon_size", iconSize() >= 64 ? 44 : iconSize() + 10).apply();
                    if (which == 4) edit.putBoolean("labels", !showLabels()).apply();
                    if (which == 5) loadApps();
                    renderHome();
                })
                .setNegativeButton("Fechar", null)
                .show();
    }

    @Override public void onBackPressed() {
        if (drawerOpen || !currentQuery.isEmpty()) {
            drawerOpen = false;
            currentQuery = "";
            renderHome();
        } else {
            super.onBackPressed();
        }
    }
}
