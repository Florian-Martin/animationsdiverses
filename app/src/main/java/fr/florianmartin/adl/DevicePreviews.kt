package fr.florianmartin.adl

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * Annotation multi-preview pour afficher un composable sur différentes tailles d'écran typiques.
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Small Phone", device = Devices.NEXUS_5, showBackground = true)
@Preview(name = "Medium Phone (Pixel 5)", device = Devices.PIXEL_5, showBackground = true)
@Preview(name = "Large Phone (Pixel XL)", device = Devices.PIXEL_XL, showBackground = true)
@Preview(name = "Foldable Inner", device = Devices.PIXEL_FOLD, showBackground = true)
@Preview(name = "Tablet (Nexus 10)", device = Devices.NEXUS_10, showBackground = true)
annotation class DevicePreviews