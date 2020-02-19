import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.wimmy.fragment.*

class MainFragmentStatePagerAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> return NameFragment()
            1 -> return TagFragment()
            2 -> return LocationFragment()
            3 -> return CalFragment()
            4 -> return MapFragment()
            else -> return NameFragment()
        }
    }

    override fun getCount(): Int = fragmentCount
}