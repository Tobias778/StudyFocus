package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import au.edu.jcu.assessment.utilityapp.data.QuoteRepository
import au.edu.jcu.assessment.utilityapp.data.QuoteService
import au.edu.jcu.assessment.utilityapp.ui.screens.FocusApp
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import au.edu.jcu.assessment.utilityapp.viewmodel.FocusViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inject the API repository into the screen-level ViewModel.
        val repository = QuoteRepository(QuoteService.create())
        val viewModel = ViewModelProvider(
            this,
            FocusViewModel.Factory(repository)
        )[FocusViewModel::class.java]

        setContent {
            MobileTech_UtilityAppTheme {
                FocusApp(viewModel)
            }
        }
    }
}
