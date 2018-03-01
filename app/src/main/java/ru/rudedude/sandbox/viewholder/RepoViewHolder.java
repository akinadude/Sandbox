package ru.rudedude.sandbox.viewholder;

import android.view.View;
import android.widget.TextView;

import ru.rudedude.sandbox.R;
import ru.rudedude.sandbox.model.GithubRepo;

public class RepoViewHolder extends BaseViewHolder {

    private TextView nameTv;
    private TextView descriptionTv;
    private TextView languageTv;
    private TextView starsNumberTv;

    public RepoViewHolder(View itemView) {
        super(itemView);
        nameTv = (TextView) itemView.findViewById(R.id.text_repo_name);
        descriptionTv = (TextView) itemView.findViewById(R.id.text_repo_description);
        languageTv = (TextView) itemView.findViewById(R.id.text_language);
        starsNumberTv = (TextView) itemView.findViewById(R.id.text_stars);
    }

    @Override
    public void bind(GithubRepo repo) {
        nameTv.setText(repo.name);
        descriptionTv.setText(repo.description);
        languageTv.setText(repo.language);
        starsNumberTv.setText(String.valueOf(repo.stargazersCount));
    }
}