GIT_API_URL = 'https://github.com/api/v3'
GIT_ORG = '<GitHub org>'
GIT_REPO_NAME = '<GIT repo name>'
GITHUB_TOKEN = "<GitHub API Token>"

def protect_branch_with_check_in_access_to_few_teams_with_pr_review(){
    sh """
        curl -X PUT \
         -H 'Accept: application/vnd.github.luke-cage-preview+json' \
         -H "Authorization: Token ${GITHUB_TOKEN}" \
         -d '{
            "restrictions": {"users": [], "teams": ["team1", "team2"]},
            "required_status_checks": null,
            "enforce_admins": null,
            "required_pull_request_reviews": { "dismiss_stale_reviews": true, "required_approving_review_count": 1 }
        }' "${GIT_API_URL}/repos/${GIT_ORG}/${GIT_REPO_NAME}/branches/${TARGET_BRANCH}/protection"
    
    """   
}

def protect_branch_only_with_pr_review(){
    sh """
        curl -X PUT \
         -H 'Accept: application/vnd.github.luke-cage-preview+json' \
         -H "Authorization: Token ${GITHUB_TOKEN}" \
         -d '{
            "restrictions": null,
            "required_status_checks": null,
            "enforce_admins": null,
            "required_pull_request_reviews": { "dismiss_stale_reviews": true, "required_approving_review_count": 1 }
        }' "${GIT_API_URL}/repos/${GIT_ORG}/${GIT_REPO_NAME}/branches/${TARGET_BRANCH}/protection"
    
    """   
}

def protect_branch_only_with_check_in_access_to_few_teams(){
    sh """
        curl -X PUT \
         -H 'Accept: application/vnd.github.luke-cage-preview+json' \
         -H "Authorization: Token ${GITHUB_TOKEN}" \
         -d '{
            "restrictions": {"users": [], "teams": ["team1", "team2"]},
            "required_status_checks": null,
            "enforce_admins": null,
            "required_pull_request_reviews": null
        }' "${GIT_API_URL}/repos/${GIT_ORG}/${GIT_REPO_NAME}/branches/${TARGET_BRANCH}/protection"
    
    """   
}
