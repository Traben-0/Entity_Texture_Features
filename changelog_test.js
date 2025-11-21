import fs from "node:fs"

const tagVersion = process.env.TAG_NAME.substring(1);

function readProp(key) {
  const raw = fs.readFileSync("gradle.properties", "utf8");
  const lines = raw.split("\n");

  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith("#")) continue;
    const idx = trimmed.indexOf("=");
    if (idx === -1) continue;
    const k = trimmed.slice(0, idx).trim();
    const v = trimmed.slice(idx + 1).trim();
    if (k === key) return v;
  }
  return "";
}

const version = readProp("mod_version");
if (version !== tagVersion) throw new Error(`found project version: ${version} does not match tagged version: ${tagVersion}`)
console.log(`version: ${version} is OK`);

function changelog(version) {
  const raw = fs.readFileSync("CHANGELOG.MD", "utf8");
  const lines = raw.split("\n");
  let thisVersion = "";
  let inside = false;
  for (const rawLine of lines) {
    const line = rawLine.trim();
    if (line.startsWith("[")) {
      if (line === `[${version}]`) {
        inside = true;
        continue;
      } else break;
    }
    if (inside) thisVersion += `${line}\n`;
  }
  return thisVersion;
}

const thisChangelog = changelog(version);
if (thisChangelog.length === 0) throw new Error(`No changelog found for version: ${version}`);

console.log(`changelog and version test PASS: \n[${version}]\n${thisChangelog}`);
