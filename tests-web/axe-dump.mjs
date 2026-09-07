import { chromium } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";
import { writeFileSync } from "node:fs";

const browser = await chromium.launch();
const context = await browser.newContext();
const page = await context.newPage();
await page.addInitScript(() => localStorage.clear());
await page.goto("http://127.0.0.1:3210/");

const screens = [
  "screen-home", "screen-library", "screen-folders", "screen-search", "screen-settings",
];
const out = {};
for (const s of screens) {
  await page.locator(`.bottom-nav button[data-screen=${s}]`).click();
  const results = await new AxeBuilder({ page })
    .withTags(["wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "wcag22a", "wcag22aa"])
    .analyze();
  out[s] = results.violations.map((v) => ({
    id: v.id,
    impact: v.impact,
    help: v.help,
    nodes: v.nodes.length,
    sampleTargets: v.nodes.slice(0, 3).map((n) => n.target.join(", ")),
  }));
}
writeFileSync("web-preview/.playwright-results/axe-dump.json", JSON.stringify(out, null, 2));
await browser.close();
console.log("done");
