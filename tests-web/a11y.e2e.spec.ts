import AxeBuilder from "@axe-core/playwright";
import { test, expect } from "@playwright/test";

const WCAG_TAGS = ["wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "wcag22a", "wcag22aa"];

test.describe("Sonqiva web-preview: accessibility (WCAG 2.2 AA)", () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => localStorage.clear());
    await page.goto("/");
  });

  for (const screen of [
    ["home", ".bottom-nav button[data-screen=screen-home]"],
    ["library", ".bottom-nav button[data-screen=screen-library]"],
    ["folders", ".bottom-nav button[data-screen=screen-folders]"],
    ["search", ".bottom-nav button[data-screen=screen-search]"],
    ["settings", ".bottom-nav button[data-screen=screen-settings]"],
  ]) {
    test(`${screen[0]} screen has no WCAG 2.2 AA violations`, async ({ page }) => {
      await page.locator(screen[1]).click();
      const results = await new AxeBuilder({ page })
        .withTags(WCAG_TAGS)
        .analyze();
      expect(results.violations).toEqual([]);
    });
  }

test("all song-row action buttons have accessible names", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    const rows = page.locator("#library-songs-list .song-row");
    const count = await rows.count();
    for (let i = 0; i < count; i++) {
      const btn = rows.nth(i).locator(".song-action-btn");
      const name = await btn.getAttribute("aria-label");
      expect(name, `row ${i} button accessible name`).toBeTruthy();
    }
  });

  test("library tabs are keyboard operable with arrow keys", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    const songsTab = page.locator('.tab-item[data-tab="songs"]');
    const favoritesTab = page.locator('.tab-item[data-tab="favorites"]');
    await songsTab.focus();
    await page.keyboard.press("ArrowRight");
    await expect(favoritesTab).toBeFocused();
    await page.keyboard.press("Enter");
    await expect(page.locator("#panel-favorites")).toHaveClass(/active/);
    await expect(favoritesTab).toHaveAttribute("aria-selected", "true");
    await expect(songsTab).toHaveAttribute("aria-selected", "false");
  });

  test("song rows can be played with the keyboard", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    const row = page.locator("#library-songs-list .song-row", { hasText: "Starlight Echoes" });
    await row.focus();
    await page.keyboard.press("Enter");
    await expect(page.locator("#mini-title")).toHaveText("Starlight Echoes");
  });

  test("sheet options respond to keyboard activation", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    await page.locator("#library-songs-list .song-row").first().click();
    await page.locator("#mini-player").click();
    await page.locator("#open-speed-btn").click();
    await page.locator('.speed-option[data-speed="1.5"]').focus();
    await page.keyboard.press("Enter");
    await expect(page.locator("#speed-label")).toHaveText("1.5x");
  });

  test("Escape closes sheets and restores focus", async ({ page }) => {
    await page.locator(".bottom-nav").getByRole("button", { name: "Library" }).click();
    const actionBtn = page.locator("#library-songs-list .song-row").first().locator(".song-action-btn");
    await actionBtn.click();
    await expect(page.locator("#actions-sheet")).toHaveClass(/open/);
    await page.keyboard.press("Escape");
    await expect(page.locator("#actions-sheet")).not.toHaveClass(/open/);
    await expect(actionBtn).toBeFocused();
  });
});
