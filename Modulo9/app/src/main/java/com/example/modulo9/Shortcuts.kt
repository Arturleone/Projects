import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import android.content.pm.ShortcutManager
import com.example.modulo9.ListaActivity
import com.example.modulo9.MainActivity

class ShortcutHelper {

    companion object {

        fun criarAtalhos(context: Context) {
            if (Build.VERSION.SDK_INT >= 25) {
                val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager

                    val shortcutTela1 = android.content.pm.ShortcutInfo.Builder(context, "shortcut_tela1")
                        .setShortLabel("Registro")
                        .setLongLabel("Registro")
                        .setIntent(Intent(context, MainActivity::class.java).apply {
                            action = Intent.ACTION_VIEW
                        })
                        .build()
                    val shortcutTela2 = android.content.pm.ShortcutInfo.Builder(context, "shortcut_tela2")
                        .setShortLabel("Reclamações")
                        .setLongLabel("Reclamações")
                        .setIntent(Intent(context, ListaActivity::class.java).apply {
                            action = Intent.ACTION_VIEW
                        })
                        .build()
                    val shortcutList = listOf(shortcutTela1, shortcutTela2)
                    shortcutManager.dynamicShortcuts = shortcutList
                }
            }
        }
    }
