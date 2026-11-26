bump_author_name := "7mochi-bot"
bump_author_email := "bot@osupe.ru"

build:
    ./gradlew build

check:
    ./gradlew check

test:
    ./gradlew test

format:
    ./gradlew spotlessApply

lint:
    ./gradlew spotlessCheck

clean:
    ./gradlew clean

publish-local:
    ./gradlew publishToMavenLocal

bump part="patch":
    #!/usr/bin/env bash
    set -euo pipefail

    case "{{ part }}" in patch|minor|major) ;; *) echo "usage: just bump [patch|minor|major]" >&2; exit 1 ;; esac

    if [ "$(git branch --show-current)" != master ]; then
        echo "error: just bump must run on master" >&2
        exit 1
    fi

    GIT_AUTHOR_NAME="{{ bump_author_name }}" \
    GIT_AUTHOR_EMAIL="{{ bump_author_email }}" \
    GIT_COMMITTER_NAME="{{ bump_author_name }}" \
    GIT_COMMITTER_EMAIL="{{ bump_author_email }}" \
    bump-my-version bump {{ part }}

    tag="chio-v$(grep -m1 '^version = ' build.gradle.kts | sed 's/.*"\(.*\)"/\1/')"
    git rev-parse -q --verify "refs/tags/$tag" >/dev/null || {
        echo "error: release tag was not created" >&2
        exit 1
    }

    echo "Created $tag. Review it, then push:"
    echo "  git push origin HEAD $tag"
