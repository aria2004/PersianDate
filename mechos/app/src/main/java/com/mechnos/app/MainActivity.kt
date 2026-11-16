package com.mechnos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Blue = Color(0xFF25A9F4)
private val Navy = Color(0xFF081321)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MechOSApp() }
    }
}

@Composable
fun MechOSApp() {
    var english by remember { mutableStateOf(false) }
    var dark by remember { mutableStateOf(true) }
    var page by remember { mutableStateOf(0) }
    val bg = if (dark) Navy else Color(0xFFF3F6FA)
    val surface = if (dark) Color(0xFF122337) else Color.White
    val fg = if (dark) Color(0xFFF2F7FC) else Color(0xFF15263A)
    val muted = if (dark) Color(0xFF9CB0C5) else Color(0xFF60748A)
    val fa = !english
    MaterialTheme(colorScheme = if (dark) darkColorScheme(primary=Blue, background=bg, surface=surface) else lightColorScheme(primary=Color(0xFF087FC2), background=bg, surface=surface)) {
        Scaffold(containerColor=bg, bottomBar={
            NavigationBar(containerColor=surface) {
                NavigationBarItem(selected=page==0,onClick={page=0},icon={Icon(Icons.Default.Dashboard,null)},label={Text(if(fa) "خانه" else "Home")})
                NavigationBarItem(selected=page==1,onClick={page=1},icon={Icon(Icons.Default.Calculate,null)},label={Text(if(fa) "محاسبات" else "Calculator")})
                NavigationBarItem(selected=page==2,onClick={page=2},icon={Icon(Icons.Default.MenuBook,null)},label={Text(if(fa) "فرمول‌ها" else "Formulas")})
                NavigationBarItem(selected=page==3,onClick={page=3},icon={Icon(Icons.Default.Settings,null)},label={Text(if(fa) "تنظیمات" else "Settings")})
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().background(bg).padding(padding).padding(horizontal=18.dp)) {
                Row(Modifier.fillMaxWidth().padding(top=18.dp,bottom=18.dp), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).background(Blue, RoundedCornerShape(13.dp)), contentAlignment=Alignment.Center) {
                            Icon(Icons.Default.SettingsSuggest, null, tint=Color.White, modifier=Modifier.size(28.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("MechOS", color=fg, fontWeight=FontWeight.Black, fontSize=22.sp)
                            Text("ENGINEERING TOOLBOX", color=muted, fontSize=9.sp, letterSpacing=1.5.sp)
                        }
                    }
                    Surface(color=surface, shape=RoundedCornerShape(20.dp), onClick={english=!english}) {
                        Text(if(english) "EN | فارسی" else "فارسی | EN", color=fg, modifier=Modifier.padding(horizontal=13.dp,vertical=9.dp), fontWeight=FontWeight.Bold, fontSize=12.sp)
                    }
                }
                when(page) {
                    0 -> HomePage(fa, fg, muted, surface) { page=it }
                    1 -> CalculatorPage(fa, fg, muted, surface)
                    2 -> FormulaPage(fa, fg, muted, surface)
                    else -> SettingsPage(fa, dark, fg, muted, surface, {dark=it}, {english=it})
                }
            }
        }
    }
}

@Composable
fun HomePage(fa:Boolean, fg:Color, muted:Color, surface:Color, go:(Int)->Unit) {
    LazyColumn(verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item {
            Text(if(fa) "سلام، مهندس! 👋" else "Hello, Engineer! 👋", color=fg, fontSize=25.sp, fontWeight=FontWeight.Bold)
            Text(if(fa) "ابزارهای محاسباتی همیشه همراهت هستند." else "Your engineering tools, always at hand.", color=muted, fontSize=13.sp)
        }
        item {
            Surface(color=Color(0xFF103B60), shape=RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(if(fa) "جعبه‌ابزار مهندسی مکانیک" else "MECHANICAL ENGINEERING TOOLKIT", color=Color(0xFF9BD8FF), fontSize=10.sp, fontWeight=FontWeight.Bold)
                    Text(if(fa) "از فرمول تا پاسخ،\nمرحله‌به‌مرحله." else "From formula to answer,\nstep by step.", color=Color.White, fontSize=22.sp, fontWeight=FontWeight.Bold, modifier=Modifier.padding(top=9.dp))
                    Text(if(fa) "نسخه ۱.۰ • قابل استفاده آفلاین" else "Version 1.0 • Works offline", color=Color(0xFFB8D2E8), fontSize=11.sp, modifier=Modifier.padding(top=12.dp))
                }
            }
        }
        item { Text(if(fa) "ابزارهای سریع" else "QUICK TOOLS", color=muted, fontSize=11.sp, fontWeight=FontWeight.Bold) }
        item {
            Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
                ToolCard(Icons.Default.Thermostat, if(fa) "ترمودینامیک" else "Thermodynamics", if(fa) "کیفیت، انرژی و خواص" else "Quality, energy & properties", surface, fg, muted) { go(1) }
                ToolCard(Icons.Default.Architecture, if(fa) "استاتیک و مقاومت مصالح" else "Statics & Strength", if(fa) "نیرو، تنش و کرنش" else "Forces, stress & strain", surface, fg, muted) { go(1) }
                ToolCard(Icons.Default.Functions, if(fa) "بانک فرمول‌ها" else "Formula Library", if(fa) "روابط کلیدی مهندسی" else "Essential engineering equations", surface, fg, muted) { go(2) }
            }
        }
    }
}

@Composable
fun ToolCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title:String, desc:String, surface:Color, fg:Color, muted:Color, click:()->Unit) {
    Surface(color=surface, shape=RoundedCornerShape(17.dp), modifier=Modifier.fillMaxWidth().clickable{click()}) {
        Row(Modifier.padding(16.dp), verticalAlignment=Alignment.CenterVertically) {
            Icon(icon,null,tint=Blue,modifier=Modifier.size(28.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) { Text(title,color=fg,fontWeight=FontWeight.Bold); Text(desc,color=muted,fontSize=12.sp) }
            Icon(Icons.Default.ChevronRight,null,tint=muted)
        }
    }
}

@Composable
fun CalculatorPage(fa:Boolean, fg:Color, muted:Color, surface:Color) {
    var category by remember { mutableStateOf(0) }
    var a by remember { mutableStateOf("100") }
    var b by remember { mutableStateOf("20") }
    val x = a.toDoubleOrNull()
    val y = b.toDoubleOrNull()
    val result = when(category) {
        0 -> if(x!=null && y!=null && y!=0.0) "%.4f".format(Locale.US, x/y) else "—"
        1 -> if(x!=null && y!=null) "%.4f".format(Locale.US, x*y) else "—"
        2 -> if(x!=null && y!=null) "%.4f".format(Locale.US, x+y) else "—"
        else -> if(x!=null && y!=null) "%.4f".format(Locale.US, x-y) else "—"
    }
    Column(verticalArrangement=Arrangement.spacedBy(14.dp)) {
        Text(if(fa) "ماشین‌حساب مهندسی" else "Engineering Calculator", color=fg, fontSize=23.sp, fontWeight=FontWeight.Bold)
        Text(if(fa) "محاسبات پایه با دقت اعشاری" else "Basic calculations with decimal precision", color=muted, fontSize=13.sp)
        Row(horizontalArrangement=Arrangement.spacedBy(7.dp)) {
            listOf(if(fa)"تقسیم" else "Divide",if(fa)"ضرب" else "Multiply",if(fa)"جمع" else "Add",if(fa)"تفریق" else "Subtract").forEachIndexed { i,s ->
                FilterChip(selected=category==i,onClick={category=i},label={Text(s,fontSize=11.sp)})
            }
        }
        NumberField(if(fa) "عدد اول" else "First value",a,{a=it},surface,fg)
        NumberField(if(fa) "عدد دوم" else "Second value",b,{b=it},surface,fg)
        Surface(color=Color(0xFF103B60),shape=RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text(if(fa) "نتیجه محاسبه" else "CALCULATION RESULT",color=Color(0xFF9BD8FF),fontSize=11.sp)
                Text(result,color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp))
            }
        }
        Text(if(fa) "نکته: ورودی‌ها را با اعداد انگلیسی وارد کن." else "Tip: Enter numbers using digits 0–9.",color=muted,fontSize=12.sp)
    }
}

@Composable
fun NumberField(label:String,value:String,onValue:(String)->Unit,surface:Color,fg:Color) {
    Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
        Text(label,color=fg,fontSize=13.sp,fontWeight=FontWeight.Medium)
        OutlinedTextField(value=value,onValueChange=onValue,modifier=Modifier.fillMaxWidth(),singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),shape=RoundedCornerShape(14.dp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=Blue,unfocusedBorderColor=Color.Gray.copy(alpha=.4f)))
    }
}

@Composable
fun FormulaPage(fa:Boolean, fg:Color, muted:Color, surface:Color) {
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text(if(fa) "فرمول‌های پرکاربرد" else "Essential Formulas",color=fg,fontSize=23.sp,fontWeight=FontWeight.Bold)
        Text(if(fa) "روابط پایه برای شروع محاسبات" else "Core equations to get started",color=muted,fontSize=13.sp)
        FormulaCard(if(fa)"قانون دوم نیوتن" else "Newton's Second Law","F = ma",if(fa)"نیرو = جرم × شتاب" else "Force = mass × acceleration",surface,fg,muted)
        FormulaCard(if(fa)"تنش نرمال" else "Normal Stress","σ = F / A",if(fa)"نیرو تقسیم بر سطح مقطع" else "Force divided by cross-sectional area",surface,fg,muted)
        FormulaCard(if(fa)"چگالی" else "Density","ρ = m / V",if(fa)"جرم تقسیم بر حجم" else "Mass divided by volume",surface,fg,muted)
        FormulaCard(if(fa)"قانون اول ترمودینامیک" else "First Law of Thermodynamics","ΔU = Q − W",if(fa)"تغییر انرژی داخلی = گرمای ورودی − کار خروجی" else "Change in internal energy = heat in − work out",surface,fg,muted)
        FormulaCard(if(fa)"توان مکانیکی" else "Mechanical Power","P = Tω",if(fa)"گشتاور × سرعت زاویه‌ای" else "Torque × angular velocity",surface,fg,muted)
        FormulaCard(if(fa)"سرعت خطی" else "Linear Velocity","v = ωr",if(fa)"سرعت زاویه‌ای × شعاع" else "Angular velocity × radius",surface,fg,muted)
    }
}

@Composable
fun FormulaCard(title:String,formula:String,desc:String,surface:Color,fg:Color,muted:Color) {
    Surface(color=surface,shape=RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(15.dp),verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) { Text(title,color=fg,fontWeight=FontWeight.Bold); Text(desc,color=muted,fontSize=11.sp) }
            Text(formula,color=Blue,fontSize=17.sp,fontWeight=FontWeight.Bold)
        }
    }
}

@Composable
fun SettingsPage(fa:Boolean,dark:Boolean,fg:Color,muted:Color,surface:Color,onDark:(Boolean)->Unit,onEnglish:(Boolean)->Unit) {
    Column(verticalArrangement=Arrangement.spacedBy(15.dp)) {
        Text(if(fa) "تنظیمات" else "Settings",color=fg,fontSize=23.sp,fontWeight=FontWeight.Bold)
        Surface(color=surface,shape=RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(if(fa) "ظاهر برنامه" else "Appearance",color=fg,fontWeight=FontWeight.Bold)
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                    Text(if(fa) "حالت تاریک" else "Dark mode",color=muted)
                    Switch(checked=dark,onCheckedChange=onDark)
                }
            }
        }
        Surface(color=surface,shape=RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(if(fa) "زبان برنامه" else "App language",color=fg,fontWeight=FontWeight.Bold)
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                    Text("فارسی / English",color=muted)
                    TextButton(onClick={onEnglish(!fa)}) { Text(if(fa) "English" else "فارسی") }
                }
            }
        }
        Text("MechOS 1.0 • Mechanical Engineering Toolbox",color=muted,fontSize=11.sp)
    }
}
