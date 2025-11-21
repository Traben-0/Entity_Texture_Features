// Credit to Ewan Howell for writing the initial script

import config from "./uploader_config.json" with { type: "json" }
import fs from "node:fs"

// read env vars
const cfToken = process.env.CF_TOKEN;
const cfCookie = process.env.CF_COOKIE;
const modrinth = process.env.MODRINTH;

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

function makeForm(data) {
  const form = new FormData
  for (const [k, v] of Object.entries(data)) {
    if (v === undefined) continue
    if (typeof v === "object") {
      form.append(k, JSON.stringify(v))
    } else {
      form.append(k, v)
    }
  }
  return form
}

// CurseForge Get Version Ids

const cfVersionsRequest = await fetch(`https://authors.curseforge.com/_api/project-files/${config.curseforge}/create-project-file-form-data`, {
  headers: {
    cookie: cfCookie
  }
})

if (!cfVersionsRequest.ok) {
  throw new Error("CurseForge: Failed getting version list" + await cfVersionsRequest.text())
}

const cfVersions = await cfVersionsRequest.json()
//console.log('cfVersions:', JSON.stringify(cfVersions, null, 2));

const cfLoaders = cfVersions.versionsData[1].flatMap(e => e.choices)
const cfMcVersions = cfVersions.versionsData[3].flatMap(e => e.choices)

// Upload Files

for (const file of config.files) {
  const name = `${file.versions[0]}-${file.loaders[0].toLowerCase()}`

  try {
    const content = fs.readFileSync(`jars/${config.id}-${version}-${name}.jar`)
    const blob = new Blob([content], {
      type: "application/java-archive"
    })

    // CurseForge Upload File

    const cfForm = makeForm({
      metadata: {
        changelog: thisChangelog,
        changelogType: "markdown",
        displayName: `${file.loaders[0]} - ${file.versions[0]} - ${version}`,
        gameVersions: [
          9638,
          ...file.loaders.map(e => cfLoaders.find(v => v.name === e).id),
          ...file.versions.map(e => cfMcVersions.find(v => v.name === e).id)
        ],
        releaseType: "release"
      }
    })

    cfForm.append("file", blob, `${config.id}_${name}-${version}.jar`)

    const cfRequest = await fetch(`https://minecraft.curseforge.com/api/projects/${config.curseforge}/upload-file`, {
      method: "POST",
      headers: {
        "X-Api-Token": cfToken
      },
      body: cfForm
    })

    if (!cfRequest.ok) {
      throw new Error(`CurseForge: Failed to upload "${name}"`) // - ${await cfRequest.text()}`)
    }

    console.log(`CurseForge: File "${name}" uploaded`)

    // Modrinth Upload File

    const mrForm = makeForm({
      data: {
        name: `${file.loaders[0]} - ${file.versions[0]}`,
        // addressable version for use as dependency
        version_number: `${version}-${file.loaders[0].toLowerCase()}-${file.versions[0]}`, // 7.0.5-fabric-1.21.9
        changelog: thisChangelog,
        dependencies: config.dependency_modrinth ? [{
          project_id: config.dependency_modrinth,
          dependency_type: "required"
        }] : [],
        game_versions: file.versions,
        version_type: "release",
        loaders: file.loaders.map(e => e.toLowerCase()),
        featured: false,
        project_id: config.modrinth,
        file_parts: ["file"],
        primary_file: "file"
      }
    })

    mrForm.append("file", blob, `${config.id}_${name}-${version}.jar`)

    const mrRequest = await fetch("https://api.modrinth.com/v2/version", {
      method: "POST",
      headers: {
        Authorization: modrinth
      },
      body: mrForm
    }).then(e => e.json())

    if (mrRequest.error) {
      throw new Error(`Modrinth: Failed to upload "${name}"`) // - ${JSON.stringify(mrRequest)}`)
    }

    console.log(`Modrinth: File "${name}" uploaded`)
  } catch (error) {
    console.error(`File "${name}" FAILED!!!`, error)
  }
}

console.log("Finished uploading files")