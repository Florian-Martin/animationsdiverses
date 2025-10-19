package fr.florianmartin.adl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar // Explicit import for clarity

@Composable
fun DatePickerView(
    modifier: Modifier = Modifier,
    initialSelectedDate: Calendar = Calendar.getInstance(),
    onDateSelected: (Calendar) -> Unit
) {
    var currentMonthCalendar by remember { mutableStateOf(getCalendarInstance(initialSelectedDate.timeInMillis)) }
    var selectedDate by remember { mutableStateOf(getCalendarInstance(initialSelectedDate.timeInMillis)) }

    // Update internal state if initialSelectedDate changes from outside
    LaunchedEffect(initialSelectedDate) {
        currentMonthCalendar = getCalendarInstance(initialSelectedDate.timeInMillis)
        selectedDate = getCalendarInstance(initialSelectedDate.timeInMillis)
    }

    val monthNameFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val selectedDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Top Section: Label, Selected Date, Time
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Début",
                fontSize = 16.sp,
                color = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
            DatePickerChip(
                text = selectedDateFormat.format(selectedDate.time),
                isSelected = true, // This chip is always "selected" in this context
                onClick = { /* Could open a full screen dialog date picker */ }
            )
            Spacer(modifier = Modifier.width(8.dp))
            TimePickerChip(
                text = timeFormat.format(selectedDate.time),
                onClick = { /* Could open a time picker dialog */ }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Month Navigation Header
        MonthNavigationHeader(
            currentMonth = monthNameFormat.format(currentMonthCalendar.time)
                .replaceFirstChar { it.uppercase() },
            onPreviousMonth = {
                currentMonthCalendar =
                    getCalendarInstance(currentMonthCalendar.timeInMillis).apply {
                        add(Calendar.MONTH, -1)
                    }
            },
            onNextMonth = {
                currentMonthCalendar =
                    getCalendarInstance(currentMonthCalendar.timeInMillis).apply {
                        add(Calendar.MONTH, 1)
                    }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Days of the Week Header
        DaysOfWeekHeader()

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        CalendarGrid(
            calendar = currentMonthCalendar,
            selectedDate = selectedDate,
            onDateClick = { day ->
                val newSelectedDate = getCalendarInstance(currentMonthCalendar.timeInMillis).apply {
                    set(Calendar.DAY_OF_MONTH, day)
                }
                selectedDate = newSelectedDate
                onDateSelected(newSelectedDate) // Notify callback
            }
        )
    }
}

@Composable
fun DatePickerChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
    val backgroundColor =
        if (isSelected) Color.Transparent else Color.Transparent // Or a light fill if needed

    Surface(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun TimePickerChip(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .background(Color(0xFFF0F0F0), RoundedCornerShape(50)) // Light gray background
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
        color = Color.Transparent // Surface itself is transparent, background modifier handles color
    ) {
        Text(
            text = text,
            color = Color.DarkGray,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}


@Composable
fun MonthNavigationHeader(
    currentMonth: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = currentMonth,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Row {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Mois précédent",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Mois suivant",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    val daysOfWeek = listOf("LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround // Distributes space evenly
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f) // Each day takes equal width
            )
        }
    }
}

@Composable
fun CalendarGrid(
    calendar: Calendar,
    selectedDate: Calendar,
    onDateClick: (Int) -> Unit // Returns the day of the month
) {
    val tempCalendar = getCalendarInstance(calendar.timeInMillis)
    tempCalendar.set(Calendar.DAY_OF_MONTH, 1) // Start at the first day of the month

    val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) // Sunday = 1, Saturday = 7
    // Adjust to make Monday the first day (Monday = 1, Sunday = 7)
    val normalizedFirstDayOfWeek = if (firstDayOfWeek == Calendar.SUNDAY) 7 else firstDayOfWeek - 1

    val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val previousMonthCalendar =
        getCalendarInstance(tempCalendar.timeInMillis).apply { add(Calendar.MONTH, -1) }
    val daysInPreviousMonth = previousMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val gridItems = mutableListOf<DatePicker>()

    // Add days from previous month for padding
    for (i in 0 until normalizedFirstDayOfWeek - 1) { // -1 because day numbers start from 1
        gridItems.add(
            DatePicker(
                day = daysInPreviousMonth - (normalizedFirstDayOfWeek - 2 - i), // -2 because of 0-index and day numbers
                isCurrentMonth = false
            )
        )
    }

    // Add days of the current month
    for (day in 1..daysInMonth) {
        gridItems.add(DatePicker(day = day, isCurrentMonth = true))
    }

    // Add days from next month for padding (to fill up to 6 weeks for consistency)
    val remainingCells = (7 * 6) - gridItems.size // Assuming a 6-week display for consistent height
    for (i in 1..remainingCells) {
        gridItems.add(DatePicker(day = i, isCurrentMonth = false))
    }

    // Display in a 7-column grid
    Column {
        gridItems.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                week.forEach { dayItem ->
                    DayCell(
                        day = dayItem.day,
                        isCurrentMonth = dayItem.isCurrentMonth,
                        isSelected = dayItem.isCurrentMonth &&
                                calendar.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                calendar.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                                dayItem.day == selectedDate.get(Calendar.DAY_OF_MONTH),
                        onClick = {
                            if (dayItem.isCurrentMonth) {
                                onDateClick(dayItem.day)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp)) // Space between weeks
        }
    }
}

data class DatePicker(val day: Int, val isCurrentMonth: Boolean)

@Composable
fun DayCell(
    day: Int,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isCurrentMonth -> Color.Black
        else -> Color.LightGray // Days from other months
    }

    Box(
        modifier = modifier
            .aspectRatio(1f) // Make cell a square
            .padding(2.dp) // Padding around each cell
            .clip(CircleShape) // Circular selection, or RoundedCornerShape(4.dp) for slight rounding
            .background(backgroundColor)
            .clickable(
                enabled = isCurrentMonth,
                onClick = onClick
            ), // Only current month days are clickable
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Helper to get a new Calendar instance from millis to avoid modifying the original
fun getCalendarInstance(timeInMillis: Long): Calendar {
    return Calendar.getInstance().apply {
        this.timeInMillis = timeInMillis
    }
}

// --- Previews ---

@Preview(showBackground = true, name = "Date Picker View - Default")
@Composable
fun DefaultDatePickerViewPreview() {
    MaterialTheme { // Wrap with MaterialTheme for previews to get default colors
        DatePickerView(onDateSelected = {})
    }
}

@Preview(showBackground = true, name = "Date Picker View - Specific Date")
@Composable
fun SpecificDateDatePickerViewPreview() {
    MaterialTheme {
        val specificCalendar = Calendar.getInstance().apply {
            set(2024, Calendar.SEPTEMBER, 4) // Note: Calendar.MONTH is 0-indexed
        }
        DatePickerView(
            initialSelectedDate = specificCalendar,
            onDateSelected = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 380, name = "Date Picker View - Dec 2024")
@Composable
fun DecemberDatePickerViewPreview() {
    MaterialTheme {
        val specificCalendar = Calendar.getInstance().apply {
            set(2024, Calendar.DECEMBER, 15)
        }
        DatePickerView(
            initialSelectedDate = specificCalendar,
            onDateSelected = {}
        )
    }
}

//@Preview(showBackground = true, name = "Individual Date Picker Chip")
@DevicePreviews
@Composable
fun DatePickerChipPreview() {
    MaterialTheme {
        Row(modifier = Modifier.padding(8.dp)) {
            DatePickerChip(text = "31 sept. 2024", isSelected = true, onClick = {})
            Spacer(Modifier.width(8.dp))
            DatePickerChip(text = "01 oct. 2024", isSelected = false, onClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Individual Time Picker Chip")
@Composable
fun TimePickerChipPreview() {
    MaterialTheme {
        TimePickerChip(text = "08:15", onClick = {})
    }
}