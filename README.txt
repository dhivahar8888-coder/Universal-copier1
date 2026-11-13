UNIVERSAL COPIER — ANDROID PROJECT

This is source code plus a GitHub Actions cloud-build workflow. It is NOT an APK yet.

Build without a PC:
1. On your phone, open github.com in Chrome and sign in.
2. Create a new repository named UniversalCopier. Set it to Public or Private.
3. Upload the contents of this ZIP (including the hidden .github folder). If mobile upload hides .github, use the GitHub website's Add file > Create new file to add .github/workflows/build.yml, or tell me and I will guide you.
4. Open the repository's Actions tab. Run "Build APK" (or wait for the first push build).
5. Open the completed workflow run and download the artifact named UniversalCopier-debug-apk.
6. Extract the artifact ZIP and install app-debug.apk. Allow installation from your browser/files app if Android asks.
7. Open Universal Copier > Open Accessibility Settings > enable Universal Copier.

LIMITATIONS / PRIVACY
- Accessibility access is powerful. This app uses it to read text exposed by the foreground app only after you tap COPY. It does not send text anywhere.
- Some apps (including some Discord/X views) may not expose their text to accessibility, so this cannot guarantee copying arbitrary text from every app.
- This project is a basic prototype and has not been device-tested here. Android may show an accessibility warning.
