# 024 — Android/backend — Evidence

Implemented configurable URL, Internet permission, bounded HTTP GET/POST, background execution, GET cache and Android UI calls for Comic Vine/Deadpool. Static inspection only: Gradle Wrapper distribution download failed with network permission denied; SDK path is inaccessible. No remote URL was configured and physical-device smoke is pending. No credentials are stored in APK sources.

Follow-up 2026-10-04: SDK/Gradle checks passed. Signed APK configured with `https://mobile-marvel-8qex.onrender.com` has the URL in DEX, no provider key, and loaded real Comic Vine images in Collection and Comparison on the AVD. Android instrumentation passed 22/22 after the last test edit. Screenshots and hash are in spec 030. Physical-device smoke and a live Groq response remain pending; the earlier paragraph records historical state.
