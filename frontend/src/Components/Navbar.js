import { Link } from "react-router-dom"

export function Navbar(){
    return(
        <>
    <Link className = 'navBarLink' to ="/">Home</Link>
    <Link className = 'navBarLink' to ="/Music">Music</Link>
    <Link className = 'navBarLink' to ="/Movies">Movies</Link>
    <Link className = 'navBarLink' to ="/TV">TV</Link>
        </>
    )
}