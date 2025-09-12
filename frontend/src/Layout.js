import { Navbar } from "./Components/Navbar"
import { Outlet } from "react-router-dom"

export function Layout(){ // makes the nav bar a constant through all pages
    return(
        <> 
            <Navbar>
                <main>
                    <Outlet/>
                </main>
            </Navbar>
        </>
    )
}