# Tony’s Java Apps (Coastal Engineering) — Java Applets Modernized

Modern, browser-native recreations of classic Coastal Engineering teaching tools originally implemented as Java applets by Prof. Robert A. (“Tony”) Dalrymple in the 1990s. The original applets were converted to HTML/JavaScript so they run directly in any modern browser, with an emphasis on preserving the original look and interactive behavior.

**Disclaimer:** Use these tools for engineering purposes at your own risk.

---

## Run online (GitHub Pages)

- https://plynett.github.io/tonys-java-apps/

---

## What’s included

### Hydrodynamics

- **Wave Calculator** — Local wave properties from deep-water inputs; dispersion relationship + Snell’s Law.
- **Linear Wave Kinematics** — Visualizes particle orbital motions for specified wave properties.
- **Superposition of Waves** — Superimpose up to four waves to show groups and standing waves.
- **Wavemaker Theory** — Piston/flap wavemaker stroke and power for a target wave in a specified depth.
- **Seiche Calculator** — Standing-wave periods in a rectangular basin.
- **Edge Waves on a Planar Beach** — First three edge-wave modes on a planar beach of specified slope.
- **Stream Function Wave Theory** — Fully nonlinear wave on constant depth (conversion currently not functional).
- **Harmonic Theory of Tides** — Explore tidal constituents individually or in combination.
- **Tidal Response of a Bay** — Keulegan method for bay tide range and phase lag.

### Coastal processes

- **Sand Transport Calculator** — Sand transport and diffusivity parameter from shallow-water values.
- **Beach Fill** — Behavior of a rectangular beach fill.
- **Single Groin on a Straight Beach** — Deposition/erosion evolution around a groin.
- **Beach Fill Calculator** — Fill volume for a target width using equilibrium profile assumptions.

---

## Repository structure

This is a static HTML/JS project (no build tooling required). Typical layout:

- `index.html` — landing page linking to the individual tools
- `*.html` — per-tool pages (e.g., `wavetheory.html`, `wavemaker.html`, `tide.html`, etc.)
- `*.js` — per-tool computational/plotting logic (e.g., `WaveTheory.js`, `Wavemaker.js`, `Tide.js`, etc.)
- `original_JAVA/` — original Java source material retained for reference

---

## Run locally

Because browsers can restrict local file access, use a small local web server:

```bash
git clone https://github.com/plynett/tonys-java-apps.git
cd tonys-java-apps
python -m http.server 8000
```

Then open:

- http://localhost:8000/

---

## Known issues / limitations

Notes from the repository:

- **Wavemaker:** wavemaker location appears incorrect, leading to non-physical animation especially at larger depths.
- **Seiche:** may not appear on some mobile devices.
- **Stream function:** not functioning (complex conversion pending).
- **Tides:** “Single” visualization issue; axis labels would be beneficial.

If you can reproduce/fix any of these, please open a PR with:
1) a minimal test case (input settings and expected behavior), and  
2) a short note on the governing equations/assumptions.

---

## References

Several tools rely on standard linear wave theory (dispersion relationship) and Snell’s Law. A commonly used reference is:

- Dean & Dalrymple, *Water Wave Mechanics for Engineers and Scientists*, World Scientific.

---

## Credits / contact

- **Original Java applets:** Prof. Robert A. Dalrymple.
- **JavaScript conversion / maintenance:** Patrick Lynett (USC).

---

## License

No explicit license is included by default. If you intend third parties to reuse/modify/distribute this code, add a `LICENSE` file (e.g., MIT/BSD/Apache-2.0) and any attribution guidance you prefer.

