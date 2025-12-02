import { useEffect } from "react";

export default function GlobalImageErrorHandler({ fallbackSrc }) {
    useEffect(() => {
        function handleError(e) {
            if (e.target.tagName === "IMG") {
                e.target.src = fallbackSrc;
            }
        }

        document.addEventListener("error", handleError, true);

        return () => {
            document.removeEventListener("error", handleError, true);
        };
    }, [fallbackSrc]);

    return null;
}
