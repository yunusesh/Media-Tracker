import './App.css'
import { HashRouter as Router, Routes, Route} from 'react-router-dom' // Allows for routing through different page files
import { Home } from './Pages/Home'
import { Movies } from './Pages/Movies'
import { Music } from './Pages/Music'
import { TV } from './Pages/TV'
import { Layout } from './Layout'

function App(){ 
  //employ the navigation bar outide of routes so it does not hide the other components
  return(
    <Router> 
      <Layout/> 
      <Routes>
          <Route path = "/" element = {<Home/>}/>
          <Route path = "/Music" element = {<Music/>}/>
          <Route path = "/Movies" element = {<Movies/>}/>
          <Route path = "/TV" element = {<TV/>}/>
      </Routes>
    </Router>

  )
  
}

export default App